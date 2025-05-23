package com.ve.edu.ucab.AtlasRelojeria.Model;

class NodoProveedor{
    private NodoProveedor next, pre;
    private Proveedor proveedor;

    /**Constructores de la clase NodoPorveedor**/
    public NodoProveedor(NodoProveedor next, NodoProveedor pre, Proveedor proveedor) {
        this.next = next;
        this.pre = pre;
        this.proveedor = proveedor;
    }
    public NodoProveedor(){
        next = null;
        pre = null;
    }

    /** Geters **/
    public NodoProveedor getNext() {
        return next;
    }

    public NodoProveedor getPre() {
        return pre;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    /**Setters**/
    public void setNext(NodoProveedor next) {
        this.next = next;
    }

    public void setPre(NodoProveedor pre) {
        this.pre = pre;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}


public class LinkedListProveedor {
    NodoProveedor head, tail;
    /**
     *  Constructor
     *  */
    public LinkedListProveedor(NodoProveedor tail, NodoProveedor head) {
        this.tail = tail;
        this.head = head;
    }

    /**
     * Inserta en la lista al usuario
     * @param proveedor
     */
    public void insertar(Proveedor proveedor) {
        NodoProveedor node = new NodoProveedor();
        node.setProveedor(proveedor);

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
        NodoProveedor temp = head;
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
        NodoProveedor temp = head;
        String data = "";
        while(temp != null){
            data += temp.getProveedor().toString()+"\n";
            temp = temp.getPre();
        }
        return data;
    }
}
