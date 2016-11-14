package fi.jamk.nfsea;

import java.io.Serializable;

/**
 * Created by h3409 on 8.11.2016.
 */

public class NFSeaMessage implements Serializable {
    private String Title;
    private String Content;
    private String Status;

    public NFSeaMessage(String title, String content, String status) {
        Title = title;
        Content = content;
        Status = status;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getStatus() { return Status; }

    public void setStatus(String status) { Status = status; }

}

