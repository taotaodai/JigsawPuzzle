package com.ttd.jigsawpuzzlev1.data;

import java.io.Serializable;
import java.util.List;

public class PuzzleContent implements Serializable {

    private final String filePath;
    //默认为APP自带资源
    private boolean isComesWith = true;
    private boolean isClassification;
    private List<PuzzleContent> children;

    public PuzzleContent(String filePath) {
        this.filePath = filePath;
    }

    public PuzzleContent(String filePath, boolean isDefaultRes) {
        this.filePath = filePath;
        this.isComesWith = isDefaultRes;
    }

    public PuzzleContent(String filePath, boolean isComesWith, boolean isClassification, List<PuzzleContent> children) {
        this.filePath = filePath;
        this.isComesWith = isComesWith;
        this.isClassification = isClassification;
        this.children = children;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isComesWith() {
        return isComesWith;
    }

    public void setChildren(List<PuzzleContent> children) {
        this.children = children;
    }

    public List<PuzzleContent> getChildren() {
        return children;
    }

    public boolean isClassification() {
        return isClassification;
    }
}
