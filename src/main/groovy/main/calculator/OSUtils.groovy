package main.calculator

class OSUtils {

    private static String OS = System.getProperty("os.name").toLowerCase()

    static boolean isWindows() {
        return (OS.indexOf("win") >= 0)
    }

    static boolean isMac() {
        return (OS.indexOf("mac") >= 0)
    }

    static boolean isUnix() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0)
    }

    static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0)
    }
}
