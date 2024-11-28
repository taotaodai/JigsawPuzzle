package com.ttd.jigsawpuzzlev1.data;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

@Entity
public class PuzzleItem implements Serializable {
    private static final long serialVersionUID = -2872002100300074326L;

    public PuzzleItem(String filePath, boolean isComesWith) {
        this.filePath = filePath;
        this.isComesWith = isComesWith;
    }

    @Keep
    public PuzzleItem(Long id, Long contentId, boolean isComesWith,
                      String filePath) {
        this.id = id;
        this.contentId = contentId;
        this.isComesWith = isComesWith;
        this.filePath = filePath;
    }

    public PuzzleItem(Long contentId, boolean isComesWith,
                      String filePath) {
        this.contentId = contentId;
        this.isComesWith = isComesWith;
        this.filePath = filePath;
    }

    public PuzzleItem() {
    }

    @Id
    private Long id;
    private Long contentId;
    private boolean isComesWith;
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public boolean isComesWith() {
        return isComesWith;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContentId() {
        return this.contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public boolean getIsComesWith() {
        return this.isComesWith;
    }

    public void setIsComesWith(boolean isComesWith) {
        this.isComesWith = isComesWith;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
