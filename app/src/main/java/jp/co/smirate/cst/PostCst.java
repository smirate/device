package jp.co.smirate.cst;

public interface PostCst {
    /** 送信間隔. */
    public final long PERIOD = 10000;

    public enum Url {
        //TODO:★★★URL決まったらここに入れる
        STREAMINFO("http://?????/"),
        DEVICETOKENID("http://????/");

        public final String val;

        private Url(String val) {
            this.val = val;
        }
    }
}
