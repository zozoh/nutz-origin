package org.nutz.origin;

import java.io.File;
import java.io.FileFilter;
import java.io.Reader;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.FileVisitor;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ZOrigin {

    private static Log log = Logs.get();

    private static File eclipseWorkspace;
    private static File projectHome;
    private static File tmplHome;
    private static Context ctx;
    private static NutProject np = null;

    static void doFile(File f) {
        // 处理文件名
        String fnm = f.getName()
                      .replaceAll("_projectName_", np.getProjectName())
                      .replaceAll("_mainName_", np.getMainName());

        String rph = Files.renamePath(Disks.getRelativePath(tmplHome, f), fnm);
        log.infof(" + %s", rph);

        // 创建目标文件
        File fDest = Files.getFile(projectHome, rph);
        Files.createFileIfNoExists(fDest);

        // 处理文件的内容
        Segment seg = Segments.read(f);
        String str = seg.render(ctx).toString();
        Files.write(fDest, str);
    }

    static void doFile(File dir, File f) {
        // 处理文件名
        String fnm = f.getName()
                      .replaceAll("_projectName_", np.getProjectName())
                      .replaceAll("_mainName_", np.getMainName());

        // 创建目标文件
        File fDest = Files.getFile(dir, fnm);
        Files.createFileIfNoExists(fDest);

        log.infof(" + %s", Disks.getRelativePath(projectHome, fDest));

        // 处理文件的内容
        Segment seg = Segments.read(f);
        String str = seg.render(ctx).toString();
        Files.write(fDest, str);
    }

    public static void main(String[] args) {
        // 得到项目配置文件
        String confPath = "origin.properties";
        if (args.length > 1)
            confPath = args[0];

        // 解析项目信息
        File fConf = Files.checkFile(confPath);
        Reader r = Streams.fileInr(fConf);
        try {
            PropertiesProxy pp = new PropertiesProxy(r).joinByKey("$include");
            np = Lang.map2Object(pp.toMap(), NutProject.class);
            ctx = Lang.context().putAll(np);
        }
        finally {
            Streams.safeClose(r);
        }

        // 开始遍历
        tmplHome = Files.checkFile(np.getTmpldir());
        projectHome = Files.createDirIfNoExists(np.getProjectHome());
        ctx.set("projectHome", projectHome.getAbsolutePath());
        log.infof("walkd tmpl : %s", np.getTmpldir());
        log.infof("target dir : %s", np.getProjectHome());
        Disks.visitFile(tmplHome, new FileVisitor() {
            public void visit(File f) {
                doFile(f);
            }
        }, new FileFilter() {
            public boolean accept(File f) {
                String fnm = f.getName();
                if (fnm.equals(".classpath") || fnm.equals(".project"))
                    return false;
                if (f.isDirectory() && fnm.matches("^(src|test)$")) {
                    return false;
                }
                return true;
            }
        });
        // 处理 src 和 test
        doJavaSourceFolder("src");
        doJavaSourceFolder("test");

        // 处理 eclise 的 .classpath 和 .project 文件
        if (!Strings.isBlank(np.getEclipseWorkspace())) {
            // 格式化 Eclipse 的 .classpath 文件中的设定字段们
            formatClasspathentries();
            // 准备工作空间
            eclipseWorkspace = Files.createDirIfNoExists(np.getEclipseWorkspace());
            log.infof("create eclipse project : %s/%s",
                      np.getEclipseWorkspace(),
                      np.getProjectName());
            // 输出项目配置文件
            doEclipseProject(".classpath");
            doEclipseProject(".project");
        }

    }

    static void formatClasspathentries() {
        if (!Strings.isBlank(np.getEclipseClasspathentries())) {
            String[] ss = Strings.splitIgnoreBlank(np.getEclipseClasspathentries(),
                                                   "\n");
            StringBuilder sb = new StringBuilder();
            for (String s : ss) {
                Pair<String> p = Pair.create(s);
                if ("src".equals(p.getName())
                    && p.getValueString().startsWith("/")) {
                    sb.append(String.format("<classpathentry"
                                                    + " combineaccessrules=\"false\""
                                                    + " kind=\"%s\" path=\"%s\"/>\n",
                                            p.getName(),
                                            p.getValueString()));
                } else {
                    sb.append(String.format("<classpathentry"
                                                    + " kind=\"%s\" path=\"%s\"/>\n",
                                            p.getName(),
                                            p.getValueString()));
                }
            }
            ctx.set("eclipseClasspathentries", sb.toString());
        }
    }

    static void doEclipseProject(String nm) {
        File f = Files.getFile(tmplHome, nm);
        if (null == f) {
            log.warnf(" !!!ignore!!! eclipse: %s", nm);
            return;
        }
        log.infof(" + eclipse: %s", nm);
        String phDest = np.getProjectName() + "/" + nm;
        File fDest = Files.getFile(eclipseWorkspace, phDest);
        Files.createFileIfNoExists(fDest);

        Segment seg = Segments.read(f);
        Files.write(fDest, seg.render(ctx).toString());
    }

    static void doJavaSourceFolder(String nm) {
        File d = Files.getFile(tmplHome, nm);
        if (d.exists()) {
            String rph = d.getName() + "/" + np.getPackages().replace('.', '/');
            File tDir = Files.createDirIfNoExists(Files.getFile(projectHome,
                                                                rph));
            for (File f : d.listFiles()) {
                doFile(tDir, f);
            }
        }
    }

}
