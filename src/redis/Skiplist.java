package redis;

import java.util.Arrays;
import java.util.Random;

/**
 * @author Emcikem
 * @create 2022/8/20
 * @desc
 */
class Skiplist {
    static final int MAX_LEVEL = 32;
    static final double P_FACTORY = 0.25;
    private SkipListNode head;
    private int maxLevel;
    private Random random;

    public Skiplist() {
        this.head = new SkipListNode(-1, MAX_LEVEL);
        this.random = new Random();
        this.maxLevel = 0;
    }

    /**
     *  返回target是否存在于跳表中。
     */
    public boolean search(int target) {
        SkipListNode curr = findCurr(target, null, true).forward[0];
        return curr != null && curr.val == target;
    }


    private SkipListNode findCurr(int num, SkipListNode[]update, boolean onlySearch) {
        if (!onlySearch) Arrays.fill(update, head);
        SkipListNode curr = this.head;
        for (int i = maxLevel - 1; i >= 0; i--) {
            while (curr.forward[i] != null && curr.forward[i].val < num) {
                curr = curr.forward[i];
            }
            if (!onlySearch) update[i] = curr;
        }
        return curr;
    }

    /**
     * 插入一个元素到跳表
     */
    public void add(int num) {
        // 不同节点的不同层可能指向num后的一个节点，所以需要一个数组
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        findCurr(num, update, false);
        int lv = randomLevel();
        maxLevel = Math.max(maxLevel, lv);
        SkipListNode newNode = new SkipListNode(num, lv);
        for (int i = 0; i < lv; i++) {
            // 新点就在[update, update.forward]之间
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    /**
     * 在跳表中删除一个值，如果 num 不存在，直接返回false. 如果存在多个 num ，删除其中任意一个即可。
     */
    public boolean erase(int num) {
        // 不同节点的不同层可能指向num结点，所以需要一个数组
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode curr = findCurr(num, update, false);
        curr = curr.forward[0]; // 可能需要删除的点
        if (curr == null || curr.val != num) {
            return false;
        }
        for (int i = 0; i < maxLevel; i++) {
            if (update[i].forward[i] != curr) {
                break;
            }
            update[i].forward[i] = curr.forward[i];
        }
        // 最高点取决于要删除的点
        while (this.head.forward[maxLevel] == curr && maxLevel > 1 && head.forward[maxLevel - 1] == null) {
            maxLevel--;
        }
        return true;
    }

    private int randomLevel() {
        int lv = 1;
        while (random.nextDouble() < P_FACTORY && lv < MAX_LEVEL) {
            lv++;
        }
        return lv;
    }
}

class SkipListNode {
    int val;
    SkipListNode[] forward;

    public SkipListNode(int val, int maxLevel) {
        this.val = val;
        this.forward = new SkipListNode[maxLevel];
    }
}


/**
 * Your Skiplist object will be instantiated and called as such:
 * Skiplist obj = new Skiplist();
 * boolean param_1 = obj.search(target);
 * obj.add(num);
 * boolean param_3 = obj.erase(num);
 */
