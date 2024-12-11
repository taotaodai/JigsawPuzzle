package com.ttd.jigsawpuzzlev1.data.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PuzzleRecord implements Serializable {
    private static final long serialVersionUID = -4242067706425145844L;
    @Id
    private Long id;
    private Long itemId;
    private String records;
    private String previewPic;

    public PuzzleRecord(Long itemId, String records) {
        this.itemId = itemId;
        this.records = records;
    }

    @Generated(hash = 638566409)
    public PuzzleRecord(Long id, Long itemId, String records, String previewPic) {
        this.id = id;
        this.itemId = itemId;
        this.records = records;
        this.previewPic = previewPic;
    }

    @Generated(hash = 1435123220)
    public PuzzleRecord() {
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getRecords() {
        return records;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPreviewPic(String previewPic) {
        this.previewPic = previewPic;
    }

    public String getPreviewPic() {
        return previewPic;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setRecords(String records) {
        this.records = records;
    }
}
