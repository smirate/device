package jp.co.smirate.cst;

public interface NotificationCst {
    public enum NotificationKey {
        AWS("default"),
        TITLE("title"),
        SMIRATE("smirate");

        public final String val;

        private NotificationKey(String val) {
            this.val = val;
        }
    }

    public enum Msg {
        TITLE("笑顔率の高い番組が放送中です！"),
        APPNAME("Smirate");

        public final String val;

        private Msg(String val) {
            this.val = val;
        }
    }
}
