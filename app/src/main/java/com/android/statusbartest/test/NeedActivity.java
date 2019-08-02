package com.android.statusbartest.test;

import java.util.Stack;

import java.util.ArrayList;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/8/1
 */
public class NeedActivity {

    /**
     * 输入一个链表，反转链表后，输出新链表的表头。
     */
    public class ListNode {
        int val;
        ListNode next = null;

        ListNode(int val) {
            this.val = val;
        }
    }

    public class Solution {
        public ListNode ReverseList(ListNode head) {
            if (head == null) {
                return null;
            }
            //链表注意pre、head、next
            ListNode next = null;
            ListNode pre = null;
            while (head != null) {
                next = head.next;
                head.next = pre;
                pre = head;
                head = next;
            }
            return pre;
        }
    }

    /**
     * 输入两个单调递增的链表，输出两个链表合成后的链表，当然我们需要合成后的链表满足单调不减规则。
     */
    public class Solution2 {
        public ListNode Merge(ListNode list1, ListNode list2) {
            //想象两条链子和一个节点和指针
            ListNode head = new ListNode(-1);
            head.next = null;
            ListNode root = head;
            while (list1 != null && list2 != null) {
                if (list1.val < list2.val) {
                    head.next = list1;
                    head = list1;
                    list1 = list1.next;
                } else {
                    head.next = list2;
                    head = list2;
                    list2 = list2.next;
                }
            }
            if (list1 != null) {
                head.next = list1;
            }
            if (list2 != null) {
                head.next = list2;
            }
            return root.next;
        }
    }


    /**
     * 输入两棵二叉树A，B，判断B是不是A的子结构。（ps：我们约定空树不是任意一个树的子结构）
     */

    public class TreeNode {
        int val = 0;
        TreeNode left = null;
        TreeNode right = null;

        public TreeNode(int val) {
            this.val = val;

        }
    }

    public class Solution3 {
        public boolean HasSubtree(TreeNode root1, TreeNode root2) {
            return true;
        }
    }

    /**
     * 看图
     */
    public class Solution4 {
        public void Mirror(TreeNode root) {

        }
    }


    /**
     * 输入一个矩阵，按照从外向里以顺时针的顺序依次打印出每一个数字，例如，如果输入如下4 X 4矩阵： 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 则依次打印出数字1,2,3,4,8,12,16,15,14,13,9,5,6,7,11,10.
     */
    public class Solution5 {
        public ArrayList<Integer> printMatrix(int[][] matrix) {
            return null;
        }
    }

    /**
     * 定义栈的数据结构，请在该类型中实现一个能够得到栈中所含最小元素的min函数（时间复杂度应为O（1））。
     */

    public class Solution6 {


        public void push(int node) {

        }

        public void pop() {

        }

        public int top() {
            return 1;
        }

        public int min() {
            return 1;
        }
    }

    /**
     * 输入两个整数序列，第一个序列表示栈的压入顺序，请判断第二个序列是否可能为该栈的弹出顺序。假设压入栈的所有数字均不相等。
     * 例如序列1,2,3,4,5是某栈的压入顺序，序列4,5,3,2,1是该压栈序列对应的一个弹出序列，但4,3,5,1,2就不可能是该压栈序列的弹出序列。（注意：这两个序列的长度是相等的）
     */
    public class Solution7 {
        public boolean IsPopOrder(int[] pushA, int[] popA) {
            return true;
        }
    }
}

