package com.ttd.jigsawpuzzlev1.ui.record;

import com.ttd.jigsawpuzzlev1.data.db.PuzzleItem;

public interface RecordListFragmentListener {
    void requestPermissionInPageRecord();

    void onRecordItemClick(PuzzleItem item);
}
