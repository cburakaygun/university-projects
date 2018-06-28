class Logger {
    static void info(String text) {
        System.out.println("[info]\t" + text);
    }

    static void input(String text) {
        System.out.println("[input]\t" + text + ":");
    }

    static void success(String text) {
        System.out.println("[success]\t" + text);
    }


    static void fail(String text) {
        System.out.println("[fail]\t" + text);
    }
}