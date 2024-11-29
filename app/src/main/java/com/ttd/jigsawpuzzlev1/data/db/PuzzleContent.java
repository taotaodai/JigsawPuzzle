package com.ttd.jigsawpuzzlev1.data.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * 拼图目录类
 */
@Entity
public class PuzzleContent implements Serializable {

    private static final long serialVersionUID = 6008790180952257834L;
    @Id
    private Long id;
    private String name;
    private String coverPath;//封面
    //默认为APP自带资源
    private boolean isComesWith = true;

    @ToMany(referencedJoinProperty = "contentId")
    private List<PuzzleItem> puzzlePicList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 944276733)
    private transient PuzzleContentDao myDao;

    public PuzzleContent(String coverPath) {
        this.coverPath = coverPath;
    }

    public PuzzleContent(String coverPath, boolean isComesWith) {
        this.coverPath = coverPath;
        this.isComesWith = isComesWith;
    }

    public PuzzleContent(String coverPath, boolean isComesWith, List<PuzzleItem> children) {
        this.coverPath = coverPath;
        this.isComesWith = isComesWith;
        this.puzzlePicList = children;
    }

    @Generated(hash = 942997694)
    public PuzzleContent(Long id, String name, String coverPath, boolean isComesWith) {
        this.id = id;
        this.name = name;
        this.coverPath = coverPath;
        this.isComesWith = isComesWith;
    }

    @Generated(hash = 211601339)
    public PuzzleContent() {
    }

    public String getCoverPath() {
        return coverPath;
    }

    public boolean isComesWith() {
        return isComesWith;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPuzzlePicList(List<PuzzleItem> puzzlePicList) {
        this.puzzlePicList = puzzlePicList;
    }

    @Keep
    public List<PuzzleItem> getPuzzlePicList() {
        return puzzlePicList;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public boolean getIsComesWith() {
        return this.isComesWith;
    }

    public void setIsComesWith(boolean isComesWith) {
        this.isComesWith = isComesWith;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1084239075)
    public synchronized void resetPuzzlePicList() {
        puzzlePicList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 667896097)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPuzzleContentDao() : null;
    }

}
