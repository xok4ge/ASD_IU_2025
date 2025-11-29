// Реализовать красно-черное дерево. Прокомментировать логику.
public class RedBlackTree {

    private Node root;
    private final boolean RED = true;
    private final boolean BLACK = false;

   // Нода
    private class Node {
        int key;
        Node left, right;
        Node parent;
        boolean color;

        Node(int key) {
            this.key = key;
            this.color = RED;
            this.left = null;
            this.right = null;
            this.parent = null;
        }
    }

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();

        tree.insert(10);
        tree.insert(29);
        tree.insert(30);
        tree.insert(15);
        tree.insert(25);
        tree.insert(7);

        System.out.println("tree after insert:");
        tree.inorderTraversal();

        tree.delete(25);
        tree.delete(7);

        System.out.println("tree after removing:");
        tree.inorderTraversal();
    }

    // Левый поворот
    private void leftRotate(Node x) {
        Node y = x.right;  
        x.right = y.left;      
        if (y.left != null)     
            y.left.parent = x;  
        y.parent = x.parent;    
        if (x.parent == null)  
            root = y;           
        else if (x == x.parent.left)    
            x.parent.left = y;
        else                            
            x.parent.right = y;
        y.left = x;             
        x.parent = y;
    }

    // Правый поворот 
    private void rightRotate(Node y) {
        Node x = y.left;   
        y.left = x.right;      
        if (x.right != null)
            x.right.parent = y;
        x.parent = y.parent;  
        if (y.parent == null)
            root = x;
        else if (y == y.parent.left)
            y.parent.left = x;
        else
            y.parent.right = x;
        x.right = y;            
        y.parent = x;
    }

    // Вставка узла
    public void insert(int key) {
        Node node = new Node(key); 
        Node parent = null;       
        Node current = root;      

        while (current != null) {   
            parent = current;         
            if (key < current.key)
                current = current.left;
            else if (key > current.key)
                current = current.right;
            else
                return; 
        }

        node.parent = parent; 
        if (parent == null)
            root = node;            
        else if (key < parent.key)
            parent.left = node;
        else
            parent.right = node;

        fixInsert(node);
    }

    // Балансировка вставки
    private void fixInsert(Node node) {
        while (node.parent != null && node.parent.color == RED) {
            Node parent = node.parent;       
            Node grandparent = parent.parent;  
            Node uncle;                          
            if (parent == grandparent.left) {   
                uncle = grandparent.right;     

                if (uncle != null && uncle.color == RED) { // 1 случай
                    parent.color = BLACK;    
                    uncle.color = BLACK;      
                    grandparent.color = RED;  
                    node = grandparent;        
                } else {                       
                    if (node == parent.right) {   // 2 случай
                        node = parent;            
                        leftRotate(node);           
                    }
                    // 3 случай
                    parent.color = BLACK;
                    grandparent.color = RED;
                    rightRotate(grandparent); 
                }
            } else {
                uncle = grandparent.left;
                if (uncle != null && uncle.color == RED) { // 1 случай
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grandparent.color = RED;
                    node = grandparent;
                } else {
                    if (node == parent.left) { // 2 случай 
                        node = parent;
                        rightRotate(node);
                    }
                    // 3 случай
                    parent.color = BLACK;
                    grandparent.color = RED;
                    leftRotate(grandparent);
                }
            }
        }
        root.color = BLACK; 
    }


    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Замена поддерева
    private void transplant(Node u, Node v) {
        if (u.parent == null)
            root = v;         
        else if (u == u.parent.left)
            u.parent.left = v;    
        else
            u.parent.right = v;     
        if (v != null)
            v.parent = u.parent;    
    }

    // Удаление узла
    public void delete(int key) {
        Node z = root;  
    
        while (z != null) {  
            if (key == z.key)   
                break;
            else if (key < z.key)   
                z = z.left;
            else
                z = z.right;
        }

        if (z == null)
            return;        

        Node y = z;           
        boolean origColor = y.color; 
        Node x;                   

        if (z.left == null) {
            x = z.right;           
            transplant(z, z.right);  
        }
        else if (z.right == null) {
            x = z.left;
            transplant(z, z.left);
        }
        else {
            y = minimum(z.right);   
            origColor = y.color;
            x = y.right;           

            if (y.parent == z) {    
                if (x != null)
                    x.parent = y;    
            } else {
                transplant(y, y.right);  
                y.right = z.right;       
                y.right.parent = y;      
            }

            transplant(z, y);        
            y.left = z.left;           
            y.left.parent = y;          
            y.color = z.color;         
        }

        if (origColor == BLACK) {  
            fixDelete(x);              
        }
    }

    // Балансировка
    private void fixDelete(Node x) {
        while (x != root && (x == null || x.color == BLACK)) {
            Node parent = x.parent;
            Node sibling;  
            if (x == parent.left) {     
                sibling = parent.right;

                if (sibling.color == RED) { // 1 случай
                    sibling.color = BLACK;
                    parent.color = RED;
                    leftRotate(parent);
                    sibling = parent.right;
                }

                if ((sibling.left == null || sibling.left.color == BLACK) &&
                        (sibling.right == null || sibling.right.color == BLACK)) { // 2 случай
                    x = parent;
                } else {
                    if (sibling.right == null || sibling.right.color == BLACK) { // 3 случай
                        if (sibling.left != null) sibling.left.color = BLACK;
                        sibling.color = RED;
                        rightRotate(sibling);
                        sibling = parent.right;
                    }
                    // 4 случай 
                    sibling.color = parent.color;
                    parent.color = BLACK;
                    if (sibling.right != null) sibling.right.color = BLACK;
                    leftRotate(parent);
                    x = root;
                }
            } else {
                sibling = parent.left;

                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    parent.color = RED;
                    rightRotate(parent);
                    sibling = parent.left;
                }

                if ((sibling.left == null || sibling.left.color == BLACK) &&
                        (sibling.right == null || sibling.right.color == BLACK)) {
                    sibling.color = RED;
                    x = parent;
                } else {
                    if (sibling.left == null || sibling.left.color == BLACK) {
                        if (sibling.right != null) sibling.right.color = BLACK;
                        sibling.color = RED;
                        leftRotate(sibling);
                        sibling = parent.left;
                    }
                    sibling.color = parent.color;
                    parent.color = BLACK;
                    if (sibling.left != null) sibling.left.color = BLACK;
                    rightRotate(parent);
                    x = root;
                }
            }
        }
        if (x != null) x.color = BLACK;
    }

    public void inorderTraversal() {
        inorderHelper(root);
        System.out.println();
    }

    private void inorderHelper(Node node) {
        if (node != null) {
            inorderHelper(node.left);
            System.out.print(node.key + (node.color == RED ? "(R) " : "(B) "));
            inorderHelper(node.right);
        }
    }
}