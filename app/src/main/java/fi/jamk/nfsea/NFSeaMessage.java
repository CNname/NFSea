package fi.jamk.nfsea;

import java.io.Serializable;

/**
 * Created by h3409 on 8.11.2016.
 */

public class NFSeaMessage implements Serializable {
    private String Title;
    private String Content;

    public NFSeaMessage(String title, String content) {
        Title = title;
        Content = content;
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

}

