package com.system.controller;


import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.system.common.BaseController;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Menu;
import com.system.entity.User;
import com.system.entity.dto.MenuDto;
import com.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@Slf4j
@RestController
public class IndexController extends BaseController {
    // 自动注入 产生验证码的对象
    @Autowired
    private Producer producer;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/captcha")
    public Result captcha() throws IOException {
        String code = producer.createText();
        String key = UUID.randomUUID().toString();

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image,"jpg", outputStream);

        // base64转码
        BASE64Encoder encoder = new BASE64Encoder();
        String str ="data:image/jpeg;base64,";
        String base64image =str + encoder.encode(outputStream.toByteArray());
        // 存储Redis中。登录输入的验证码是否正确，需要从redis去除来对比
        log.info("验证码-- {} - {}",key,code);
        redisUtil.hset(Const.CAPTCHA_KEY,key,code,120);
        Object hget = redisUtil.hget(Const.CAPTCHA_KEY, key);
        System.out.println("redis中取出来的验证码:"+hget);
//        int x = 100 / 0;

        return Result.success(MapUtil.builder().put("key",key).put("captchaImg",base64image).build());
    }


    // 登录成功后，获取用户的详细信息
    @GetMapping("/user/userInfo")
    public Result userinfo(Principal principal){
        String username = principal.getName();
        User user = userService.getByUsername(username);

        return Result.success(MapUtil.builder()
        .put("id",user.getId())
        .put("username",user.getUsername())
        .put("avatar",user.getAvatar()).map());
    }

    // 请求获得菜单数据的接口 参数Principal，是spingscurity登录后封装的authentication对象中的用户身份主题
    @GetMapping("/menu/nav")
    public Result nav(Principal principal){
        // 获取的登录用户名
        String username = principal.getName();
        User user = userService.getByUsername(username);

        // 获取权限信息
        String userAuthorityInfo = userService.getUserAuthorityInfo(user.getId());
        // tokenizeToStringArray()将字符串按照分隔符转换为数组，参数1：转换的字符串，参数2：分隔符
        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(userAuthorityInfo, ",");

        // 获得菜单信息
        List<MenuDto> navs = menuService.getCurrentUserNav(username);

        //
        return Result.success(MapUtil.builder()
        .put("authoritys",authorityInfoArray)
        .put("nav",navs).map());
    }


    //上传图片的方法
    @PostMapping("/upload")
    public Result uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) throws Exception{
        File targetFile=null;
        //上传图片成功返回的图片的路径
        String url = "";
        try {
            //获得文件名加后缀
            String filename = file.getOriginalFilename();
            if(filename!=null && filename !=""){
                //图片访问的URI
                String returnURL = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/upload/imgs/movie";
                //文件存储的位置
                String path = request.getSession().getServletContext().getRealPath("") + "upload" + File.separator + "imgs"+File.separator+"movie";

                //文件后缀
                String fileSuffix = filename.substring(filename.lastIndexOf("."));
                //新的文件名
                filename = System.currentTimeMillis()+"_"+new Random().nextInt(1000)+fileSuffix;

                //先判断文件是否存在
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String fileAdd = sdf.format(new Date());
                //获取文件夹路径
                path = path+File.separator+fileAdd+File.separator;
                System.out.println(path);
                File directory = new File(path);
                //如果文件夹不存在就创建
                if(!directory.exists()&&!directory.isDirectory()){
                    directory.mkdirs();
                }
                //将图片存入文件夹
                targetFile  = new File(directory,filename);
                //将上传的文件写到服务器上指定的文件夹
                file.transferTo(targetFile);
                String projectPath = System.getProperty("user.dir");
                System.out.println("projectPath路径"+projectPath);
                //文件复制
                String src = path+filename;
                String destDir = projectPath + File.separator +"src"+File.separator+"main"+ File.separator +"resources"+File.separator+"static"+ File.separator+"upload"+File.separator+"imgs" + File.separator +"movie"+File.separator+ fileAdd + File.separator;
                copyFile(src,destDir,filename);

                url = returnURL +"/"+ fileAdd + "/"+filename;
                return Result.success(url);
            }
            return Result.fail("图片上传失败");
        }catch(IOException e){
            e.printStackTrace();
            return Result.fail("图片上传失败");

        }
    }
    /**
     * 文件复制
     * @param src
     * @param destDir
     * @param fileName
     * @throws IOException
     */
    public void copyFile(String src,String destDir,String fileName) throws IOException{
        FileInputStream in=new FileInputStream(src);
        File fileDir = new File(destDir);
        if(!fileDir.isDirectory()){
            fileDir.mkdirs();
        }
        File file = new File(fileDir,fileName);

        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream out=new FileOutputStream(file);
        int c;
        byte buffer[]=new byte[1024];
        while((c=in.read(buffer))!=-1){
            for(int i=0;i<c;i++){
                out.write(buffer[i]);
            }

        }
        in.close();
        out.close();
    }
}
