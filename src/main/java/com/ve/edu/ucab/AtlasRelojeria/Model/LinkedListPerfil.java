package com.ve.edu.ucab.AtlasRelojeria.Model;

class NodePerfil {
  /* Atributos de la clase "NodePerfil" - nodo */
  private NodePerfil next, pre;
  private Perfil perfil;

  /**
   *  Constructores de la clase NodePerfil
   *  */

  public NodePerfil(NodePerfil next, NodePerfil pre, Perfil perfil) {
    this.next = next;
    this.pre = pre;
    this.perfil = perfil;
  }

  public NodePerfil(){
    next = null;
    pre = null;
  }

  /** Getters */
  public NodePerfil getNext() {
    return next;
  }

  public NodePerfil getPre() {
    return pre;
  }

  public Perfil getPerfil() {
    return perfil;
  }

  /**
   *  Setters
   *  */

  public void setNext(NodePerfil next) {
    this.next = next;
  }

  public void setPre(NodePerfil pre) {
    this.pre = pre;
  }

  public void setPerfil(Perfil perfil) {
    this.perfil = perfil;
  }
}


public class LinkedListPerfil {
  NodePerfil head, tail;

  /**
   *  Constructor
   *  */
  public LinkedListPerfil() {
    this.head = null;
    this.tail = null;
  }

  /**
   * Inserta en la lista al usuario
   * @param perfil que representa el nombre del usuario que hay que registrar en el sistema, es decir. insertar en la lista enlazada
   */
  public void insertar(Perfil perfil) {
    NodePerfil node = new NodePerfil();
    node.setPerfil(perfil);

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
   * @return Perfil o null si no lo encuentra
   *
  public Perfil getInfo(String id){
    NodePerfil temp = head;
    while(temp != null){
      if(id.equals(temp.getPerfil().getId())){
        return temp.getPerfil();
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
    NodePerfil temp = head;
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
    NodePerfil temp = head;
    String data = "";
    while(temp != null){
      data += temp.getPerfil().toString()+"\n";
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
    NodePerfil temp = head;
    while( temp!= null){
      if(card.equals(temp.getPerfil().getCard())){
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
    NodePerfil temp = head;
    while(temp != null){
      if(numId.equals(temp.getPerfil().getId())){
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
  public Perfil mandarCliente(String numId){
    NodePerfil temp = head;
    Perfil mandar = null;
    while(temp != null){
      if(numId.equals(temp.getPerfil().getId())){
        mandar = temp.getPerfil();
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
    NodePerfil temp = head;
    while(temp != null){
      if(numId.equals(temp.getPerfil().getId())){
        return "\nID           " +temp.getPerfil().getId()
                +"\nNombre         " +temp.getPerfil().getNombre()
                +"\nPIN          " +temp.getPerfil().getPin()
                +"\nCard         " +temp.getPerfil().getCard()
                +"\nNum de Cuenta      " +temp.getPerfil().getNumCuenta()
                +"\nSaldo      " +temp.getPerfil().getBalance();
      }
      temp = temp.getPre();
    }
    return " ";
  }*/

  /**
   * Metodo de deposito en la clase LinkedListPerfil
   * @param numID el numero de identidad del usuario en la lista
   * @param monto el monto a depositar
   *
  public String deposito(String numID, double monto){
    NodePerfil temp = head;
    while(temp !=null){
      if(numID.equals(temp.getPerfil().getId())){
          temp.getPerfil().deposito(monto);
        return "";
      }
      temp = temp.getPre();
    }
    return "";
  }*/

  /**
   * Metodo de retiro en la clase LinkedListPerfil
   * @param usuario el usuario en la lista
   *
  public void retiro(Perfil usuario){
    NodePerfil temp = head;
    while(temp != null){
      if(usuario.getId().equals(temp.getPerfil().getId())){
        temp.setPerfil(usuario);
        break;
      }
      temp = temp.getPre();
    }
  }*/

  /**
   * Verifica que el pin y la tarjeta coincidan
   *
  public Perfil checkPinCard(String text, String text2){
    NodePerfil temp = head;
    while(temp != null){
      if(text.equals(temp.getPerfil().getPin()+"") && text2.equals(temp.getPerfil().getCard())){
        return temp.getPerfil();
      }
      temp = temp.getPre();
    }
    return null;
  }*/

}
