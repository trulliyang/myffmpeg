package com.example.shiyang1.myffmpeg.manager;

import android.util.Log;

import com.example.shiyang1.myffmpeg.node.FFGLNode;

import java.util.ArrayList;
import java.util.List;

public class NodeManager {
    List<FFGLNode> mFFGLNodeList;

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

    public void update() {
        if (null != mFFGLNodeList) {
            for (FFGLNode n: mFFGLNodeList) {
                n.update();
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
