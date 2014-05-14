package ${packages};

import org.nutz.mvc.annotation.*;
import org.nutz.mvc.ioc.provider.ComboIocProvider;

@SetupBy(${mainName}Setup.class)
@IocBy(type = ComboIocProvider.class,
        args = {"*org.nutz.ioc.loader.json.JsonLoader",
                "ioc",
                "*org.nutz.ioc.loader.annotation.AnnotationIocLoader",
                "${packages}"})
@Modules(scanPackage = true, packages = "${packages}.module")
@Localization("msg")
public class ${mainName}MainModule {

    @At("/version")
    @Ok("jsp:jsp.show_text")
    public String version() {
        return "1.0";
    }

}
