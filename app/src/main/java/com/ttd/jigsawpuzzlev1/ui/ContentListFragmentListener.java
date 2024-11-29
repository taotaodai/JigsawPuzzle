package com.ttd.jigsawpuzzlev1.ui;

import com.ttd.jigsawpuzzlev1.data.db.PuzzleContent;

public interface ContentListFragmentListener {
    void onContentClick(PuzzleContent puzzleContent);

    void requestPermission();
}
