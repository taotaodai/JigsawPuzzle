package com.ttd.jigsawpuzzlev1.ui;

import com.ttd.jigsawpuzzlev1.data.PuzzleContent;
import com.ttd.jigsawpuzzlev1.data.PuzzleItem;

import java.util.List;

public interface ContentListFragmentListener {
    void onContentClick(PuzzleContent puzzleContent);

    void requestPermission();
}
