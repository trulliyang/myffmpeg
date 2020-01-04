package com.example.shiyang1.myffmpeg.manager;

import android.util.Log;

import com.example.shiyang1.myffmpeg.node.FFGLNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParseManager {
    private List<FFGLNode> mFFGLNodeList;
    private Map<String, FFGLNode> mFFGLNodeMap;

//    private ParseManager mParseManager = null;
//
//    public Object getParseManager() {
//        if (null == mParseManager) {
//            mParseManager = new ParseManager();
//        }
//        return mParseManager;
//    }

    public void init() {
        if (null == mFFGLNodeList)
            mFFGLNodeList = new ArrayList<>();
    }

    public void destroy() {
        if (null != mFFGLNodeList) {
            for (FFGLNode n: mFFGLNodeList) {
                n.destroy();
            }
            mFFGLNodeList.clear();
            mFFGLNodeList = null;
        }
    }

    public void addFFGLNode(FFGLNode node) {
        if (null == mFFGLNodeList) {
            Log.e("shiyang", "addFFGLNode mFFGLNodeList == null");
            return;
        }

        if (null == node) {
            Log.e("shiyang", "addFFGLNode node == null");
            return;
        }
        mFFGLNodeList.add(node);
    }

    public void update(float dt) {
        if (null != mFFGLNodeList) {
            for (FFGLNode n: mFFGLNodeList) {
                n.update(dt);
            }
        }
    }

    public void render() {
        if (null != mFFGLNodeList) {
            for (FFGLNode n: mFFGLNodeList) {
                n.render();
            }
        }
    }
}
