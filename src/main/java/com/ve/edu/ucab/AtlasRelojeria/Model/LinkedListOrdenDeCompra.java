package com.ve.edu.ucab.AtlasRelojeria.Model;

class NodeOrdenDeCompra {
    private NodeOrdenDeCompra next, pre;
    private OrdenDeCompra orden;

    /**
     *  Constructores de la clase NodeOrdenDeCompra
     *  */
    public NodeOrdenDeCompra(NodeOrdenDeCompra next, NodeOrdenDeCompra pre, OrdenDeCompra orden) {
        this.next = next;
        this.pre = pre;
        this.orden = orden;
    }

    public NodeOrdenDeCompra(){
        next = null;
        pre = null;
    }

    /** Getters */
    public NodeOrdenDeCompra getNext() {
        return next;
    }

    public NodeOrdenDeCompra getPre() {
        return pre;
    }

    public OrdenDeCompra getOrden() {
        return orden;
    }

    /**
     *  Setters
     *  */
    public void setNext(NodeOrdenDeCompra next) {
        this.next = next;
    }

    public void setPre(NodeOrdenDeCompra pre) {
        this.pre = pre;
    }

    public void setOrden(OrdenDeCompra orden) {
        this.orden = orden;
    }
}


public class LinkedListOrdenDeCompra {
    NodeOrdenDeCompra head, tail;

    /**
     *  Constructor
     *  */
    public LinkedListOrdenDeCompra(NodeOrdenDeCompra head, NodeOrdenDeCompra tail) {
        this.head = head;
        this.tail = tail;
    }

    /**
     * Inserta en la lista a la orden de compra
     * @param
     */
    public void insertar(OrdenDeCompra orden) {
        NodeOrdenDeCompra node = new NodeOrdenDeCompra();
        node.setOrden(orden);

        if(head == null || tail == null) {
            head = node;
            tail = node;
        } else {
            head.setNext(node);
            node.setPre(head);
            head = node;
        }
    }

    /**
     * Retorna el tamano de la lista enlazada, es decir, el numero de clientes / usaurios del banco
     * @return el tamano de lista
     */
    public int size(){
        NodeOrdenDeCompra temp = head;
        int size = 0;
        while(temp != null){
            size++;
            temp = temp.getPre();
        }
        return size;
    }

    /**
     * Retorna toda la informacion del usuario
     * @return data
     */
    public String allData(){
        NodeOrdenDeCompra temp = head;
        String data = "";
        while(temp != null){
            data += temp.getOrden().toString()+"\n";
            temp = temp.getPre();
        }
        return data;
    }
}
