package jp.co.smirate.cst;

public interface PostCst {
    /** 送信間隔. */
    public final long PERIOD = 10000;

    public enum Url {
        STREAMINFO("http://ec2-54-172-76-222.compute-1.amazonaws.com/dataInsert.php"),
        DEVICETOKENID("http://ec2-54-172-76-222.compute-1.amazonaws.com/tokenRegist.php");

        public final String val;

        private Url(String val) {
            this.val = val;
        }
    }
}
