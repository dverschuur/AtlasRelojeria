package com.ve.edu.ucab.AtlasRelojeria.Model;

class NodeVentas {
  /* Atributos de la clase "NodeVentas" - nodo */
  private NodeVentas next, pre;
  private Ventas ventas;

  /**
   *  Constructores de la clase NodeVentas
   *  */

  public NodeVentas(NodeVentas next, NodeVentas pre, Ventas ventas) {
    this.next = next;
    this.pre = pre;
    this.ventas = ventas;
  }

  public NodeVentas(){
    next = null;
    pre = null;
  }

  /** Getters */
  public NodeVentas getNext() {
    return next;
  }

  public NodeVentas getPre() {
    return pre;
  }

  public Ventas getVentas() {
    return ventas;
  }

  /**
   *  Setters
   *  */

  public void setNext(NodeVentas next) {
    this.next = next;
  }

  public void setPre(NodeVentas pre) {
    this.pre = pre;
  }

  public void setVentas(Ventas ventas) {
    this.ventas = ventas;
  }
}


public class LinkedListVentas {
  NodeVentas head, tail;

  /**
   *  Constructor
   *  */
  public LinkedListVentas() {
    this.head = null;
    this.tail = null;
  }

  /**
   * Inserta en la lista a la venta
   * @param ventas que representa el nombre del usuario que hay que registrar en el sistema, es decir. insertar en la lista enlazada
   */
  public void insertar(Ventas ventas) {
    NodeVentas node = new NodeVentas();
    node.setVentas(ventas);

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
   * @return Ventas o null si no lo encuentra
   *
  public Ventas getInfo(String id){
    NodeVentas temp = head;
    while(temp != null){
      if(id.equals(temp.getVentas().getId())){
        return temp.getVentas();
      }
      temp = temp.getPre();
    }
    return null;
  }*/

  /**
   * Retorna el tamano de la lista enlazada, es decir, el numero de clientes / usaurios del banco
   * @return el tamano de lista
   */
  public int size(){
    NodeVentas temp = head;
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
    NodeVentas temp = head;
    String data = "";
    while(temp != null){
      data += temp.getVentas().toString()+"\n";
      temp = temp.getPre();
    }
    return data;
  }

  /**
   * Verifica que el num de tarjeta exista en la lista
   * @param card numero de card del usuario
   * @return true or false
   *
  public boolean check(String card){
    NodeVentas temp = head;
    while( temp!= null){
      if(card.equals(temp.getVentas().getCard())){
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
   *
  public boolean existe(String numId){
    NodeVentas temp = head;
    while(temp != null){
      if(numId.equals(temp.getVentas().getId())){
        return true;
      }
      temp = temp.getPre();
    }
    return false;
  }*/

  /**
   * recibe un id y devuelve el cliente al que le pertenece ese id
   * @param numId numero de identidad del usuario
   * @return mandar
   *
  public Ventas mandarCliente(String numId){
    NodeVentas temp = head;
    Ventas mandar = null;
    while(temp != null){
      if(numId.equals(temp.getVentas().getId())){
        mandar = temp.getVentas();
      }
      temp = temp.getPre();
    }
    return mandar;
  }*/

  /**
   * retorna la informacion del usuario recibiendo como parametro el id
   * @param numId el numero de identidad del usuario
   * @return informacion
   *
  public String idInfo(String numId){
    NodeVentas temp = head;
    while(temp != null){
      if(numId.equals(temp.getVentas().getId())){
        return "\nID           " +temp.getVentas().getId()
                +"\nNombre         " +temp.getVentas().getNombre()
                +"\nPIN          " +temp.getVentas().getPin()
                +"\nCard         " +temp.getVentas().getCard()
                +"\nNum de Cuenta      " +temp.getVentas().getNumCuenta()
                +"\nSaldo      " +temp.getVentas().getBalance();
      }
      temp = temp.getPre();
    }
    return " ";
  }*/

  /**
   * Metodo de deposito en la clase LinkedListVentas
   * @param numID el numero de identidad del usuario en la lista
   * @param monto el monto a depositar
   *
  public String deposito(String numID, double monto){
    NodeVentas temp = head;
    while(temp !=null){
      if(numID.equals(temp.getVentas().getId())){
          temp.getVentas().deposito(monto);
        return "";
      }
      temp = temp.getPre();
    }
    return "";
  }*/

  /**
   * Metodo de retiro en la clase LinkedListVentas
   * @param usuario el usuario en la lista
   *
  public void retiro(Ventas usuario){
    NodeVentas temp = head;
    while(temp != null){
      if(usuario.getId().equals(temp.getVentas().getId())){
        temp.setVentas(usuario);
        break;
      }
      temp = temp.getPre();
    }
  }*/

  /**
   * Verifica que el pin y la tarjeta coincidan
   *
  public Ventas checkPinCard(String text, String text2){
    NodeVentas temp = head;
    while(temp != null){
      if(text.equals(temp.getVentas().getPin()+"") && text2.equals(temp.getVentas().getCard())){
        return temp.getVentas();
      }
      temp = temp.getPre();
    }
    return null;
  }*/

}
