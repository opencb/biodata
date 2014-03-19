package org.opencb.biodata.formats.io;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/26/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileFormatException extends Exception {
    public FileFormatException(String msg) {
        super(msg);
    }

    public FileFormatException(Exception e) {
        super(e);
    }

    public FileFormatException(String msg, Exception e) {
        super(msg, e);
    }
}
