package jp.co.smirate.cst;

public interface EvixarCst {
    public enum Certification {
        APP("v4aEbiBDtFI5g3mXL7Us6RRtGkLQbAzU"),
        ACCESS("dnZ2dnZ2dnbWJ8ptzKB+nKWv+ECxeU9rASmuzct3i7kwPCl2xWbPQFpk6fjbyX8g+AKDCco7B0OKwW9X3IOdJQfdz+drqZOA6DLMoDf32y0PcnzMeKV448QvZSmOcHOhSFZpvcLuNZphTOESLknvFtYrGW10Y25ooco0LJeSI3mQG2fT9pWSMA==");

        public final String val;

        private Certification(String val) {
            this.val = val;
        }
    }

    public enum ResponseKey {
        SERVICEID("service_id"),
        EVENTID("event_id"),
        TITLE("title"),
        START("start"),
        END("end"),
        DETAIL("detail"),
        ACTORS("actors");

        public final String val;

        private ResponseKey(String val) {
            this.val = val;
        }
    }
}
