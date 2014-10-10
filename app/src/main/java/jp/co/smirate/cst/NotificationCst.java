package jp.co.smirate.cst;

public interface NotificationCst {
    public final String NOTIFICATION_KEY = "default";

    public enum Msg {
        TITLE("笑顔率の高い番組が放送中です！"),
        APPNAME("Smirate");

        public final String val;

        private Msg(String val) {
            this.val = val;
        }
    }
}
