package com.ve.edu.ucab.AtlasRelojeria.Model;

class NodeProductos {
  /* Atributos de la clase "NodeProductos" - nodo */
  private NodeProductos next, pre;
  private Producto producto;

  /**
   *  Constructores de la clase NodeProductos
   *  */

  public NodeProductos(NodeProductos next, NodeProductos pre, Producto producto) {
    this.next = next;
    this.pre = pre;
    this.producto = producto;
  }

  public NodeProductos(){
    next = null;
    pre = null;
  }

  public NodeProductos getNext() {
    return next;
  }

  public NodeProductos getPre() {
    return pre;
  }

  public Producto getProducto() {
    return producto;
  }

  /**
   *  Setters
   *  */

  public void setNext(NodeProductos next) {
    this.next = next;
  }

  public void setPre(NodeProductos pre) {
    this.pre = pre;
  }

  public void setProducto(Producto producto) {
    this.producto = producto;
  }
}


public class LinkedListProductos {
  NodeProductos head, tail;

  /**
   *  Constructor
   *  */
  public LinkedListProductos() {
    this.head = null;
    this.tail = null;
  }

  /**
   * Inserta en la lista al usuario
   * @param producto que representa el nombre del usuario que hay que registrar en el sistema, es decir. insertar en la lista enlazada
   */
  public void insertar(Producto producto) {
    NodeProductos node = new NodeProductos();
    node.setProducto(producto);

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
   *  busca la informacion del usuario
   * @param id el numero de identidad del usuario
   * @return Producto o null si no lo encuentra
   */
  public Producto getInfo(String id){
    NodeProductos temp = head;
    while(temp != null){
      if(id.equals(temp.getProducto().getId())){
        return temp.getProducto();
      }
      temp = temp.getPre();
    }
    return null;
  }

  /**
   * Retorna el tamano de la lista enlazada, es decir, el numero de clientes / usaurios del banco
   * @return el tamano de lista
   */
  public int size(){
    NodeProductos temp = head;
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
    NodeProductos temp = head;
    String data = "";
    while(temp != null){
      data += temp.getProducto().toString()+"\n";
      temp = temp.getPre();
    }
    return data;
  }

  /**
   * Verifica que el num de tarjeta exista en la lista
   * @param card numero de card del usuario
   * @return true or false

  public boolean check(String card){
    NodeProductos temp = head;
    while( temp!= null){
      if(card.equals(temp.getProducto().getCard())){
        return true;
      }
      temp = temp.getPre();
    }
    return false;
  }*/

  /**
   * Revisa si el usuario existe en el sistema con el Id
   * @param numId el numero de identidad del usuario
   * @return true or false
   */
  public boolean existe(String numId){
    NodeProductos temp = head;
    while(temp != null){
      if(numId.equals(temp.getProducto().getId())){
        return true;
      }
      temp = temp.getPre();
    }
    return false;
  }

  /**
   * recibe un id y devuelve el cliente al que le pertenece ese id
   * @param numId numero de identidad del usuario
   * @return mandar
   */
  public Producto mandarCliente(String numId){
    NodeProductos temp = head;
    Producto mandar = null;
    while(temp != null){
      if(numId.equals(temp.getProducto().getId())){
        mandar = temp.getProducto();
      }
      temp = temp.getPre();
    }
    return mandar;
  }

  /**
   * retorna la informacion del usuario recibiendo como parametro el id
   * @param numId el numero de identidad del usuario
   * @return informacion
   *
  public String idInfo(String numId){
    NodeProductos temp = head;
    while(temp != null){
      if(numId.equals(temp.getProducto().getId())){
        return "\nID           " +temp.getProducto().getId()
                +"\nNombre         " +temp.getProducto().getNombre()
                +"\nPIN          " +temp.getProducto().getPin()
                +"\nCard         " +temp.getProducto().getCard()
                +"\nNum de Cuenta      " +temp.getProducto().getNumCuenta()
                +"\nSaldo      " +temp.getProducto().getBalance();
      }
      temp = temp.getPre();
    }
    return " ";
  }*/

  /**
   * Metodo de deposito en la clase LinkedList
   * @param numID el numero de identidad del usuario en la lista
   * @param monto el monto a depositar
   *
  public String deposito(String numID, double monto){
    NodeProductos temp = head;
    while(temp !=null){
      if(numID.equals(temp.getProducto().getId())){
          temp.getProducto().deposito(monto);
        return "";
      }
      temp = temp.getPre();
    }
    return "";
  }*/

  /**
   * Metodo de retiro en la clase LinkedList
   * @param usuario el usuario en la lista
   */
  public void retiro(Producto usuario){
    NodeProductos temp = head;
    while(temp != null){
      if(usuario.getId().equals(temp.getProducto().getId())){
        temp.setProducto(usuario);
        break;
      }
      temp = temp.getPre();
    }
  }

  /**
   * Verifica que el pin y la tarjeta coincidan
   *
  public Producto checkPinCard(String text, String text2){
    NodeProductos temp = head;
    while(temp != null){
      if(text.equals(temp.getProducto().getPin()+"") && text2.equals(temp.getProducto().getCard())){
        return temp.getProducto();
      }
      temp = temp.getPre();
    }
    return null;
  }*/

}
