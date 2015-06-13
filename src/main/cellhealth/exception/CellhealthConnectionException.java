package cellhealth.exception;

import cellhealth.utils.constants.message.Error;
import cellhealth.utils.logs.L4j;

/**
 * Created by alberto on 10/06/15.
 */
public class CellhealthConnectionException extends Exception {

    private static final long serialVersionUID = 1;

    public CellhealthConnectionException(String message, Throwable cause) {
        L4j.getL4j().error(message, cause);
    }

    public CellhealthConnectionException(String message) {
        L4j.getL4j().error(message);
    }

    public CellhealthConnectionException(Throwable cause) {
        L4j.getL4j().error(Error.UNKNOWN, cause);
    }
}
