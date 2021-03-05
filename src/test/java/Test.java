import io.netty.util.internal.StringUtil;
import org.yaml.snakeyaml.Yaml;
import utils.YamlEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author yujian
 * @description
 * @create 2021-03-01 16:42
 **/
public class Test {
    public static void main(String[] args) {
        String ymlPath = "11";
        if (!StringUtil.isNullOrEmpty(ymlPath)) {
            Yaml yam = new Yaml();
            try (FileInputStream fileInputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/"+ "proxy.yml"))) {
                YamlEntity yamlEntity = yam.loadAs(fileInputStream, YamlEntity.class);
                System.out.println(yamlEntity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}