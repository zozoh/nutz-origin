package org.nutz.origin;

/**
 * 描述了一个 Project 的基本信息
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutProject {

    private String javaHome;

    private String buildDepsJars;

    private String buildOutput;

    private String projectHome;

    private String packages;

    private String projectName;

    private String mainName;

    private String tmpldir;

    private String eclipseClasspathentries;

    private String eclipseWorkspace;

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getBuildDepsJars() {
        return buildDepsJars;
    }

    public void setBuildDepsJars(String buildDepsJars) {
        this.buildDepsJars = buildDepsJars;
    }

    public String getBuildOutput() {
        return buildOutput;
    }

    public void setBuildOutput(String buildOutput) {
        this.buildOutput = buildOutput;
    }

    public String getProjectHome() {
        return projectHome;
    }

    public void setProjectHome(String projectHome) {
        this.projectHome = projectHome;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getMainName() {
        return mainName;
    }

    public void setMainName(String mainName) {
        this.mainName = mainName;
    }

    public String getTmpldir() {
        return tmpldir;
    }

    public void setTmpldir(String tmpldir) {
        this.tmpldir = tmpldir;
    }

    public String getEclipseClasspathentries() {
        return eclipseClasspathentries;
    }

    public void setEclipseClasspathentries(String classpathentries) {
        this.eclipseClasspathentries = classpathentries;
    }

    public String getEclipseWorkspace() {
        return eclipseWorkspace;
    }

    public void setEclipseWorkspace(String eclipseWorkspace) {
        this.eclipseWorkspace = eclipseWorkspace;
    }

}
