package com.ttd.jigsawpuzzlev1.ui;

import com.ttd.jigsawpuzzlev1.data.PuzzleContent;

import java.util.List;

public interface ContentListFragmentListener {
    void onContentClick(List<PuzzleContent> children);
}
