/** 
 * Proyecto: Juego de la vida.
 * Resuelve todos los aspectos del almacenamiento del DTO Usuario 
 * utilizando un ArrayList y un Hashtable persistentes en dos ficheros.
 * Colabora en el patron Fachada.
 * @since: prototipo2.0
 * @source: UsuariosDAO.java 
 * @version: 2.1 - 2017.04.09 
 * @author: ajp
 */

package accesoDatos.fichero;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import accesoDatos.DatosException;
import accesoDatos.OperacionesDAO;
import config.Configuracion;
import modelo.*;
import modelo.Usuario.RolUsuario;
import util.Fecha;

public class UsuariosDAO  implements OperacionesDAO, Persistente {

	// Requerido por el Singleton. 
	private static UsuariosDAO instancia = null;

	// Elementos de almacenamiento.
	private ArrayList<Usuario> datosUsuarios;
	private Map<String,String> equivalenciasId;
	private File fUsuarios;
	private File fEquivalId;

	/**
	 * Constructor por defecto de uso interno.
	 * Sólo se ejecutará una vez.
	 */
	private UsuariosDAO() {
		datosUsuarios = new ArrayList<Usuario>();
		equivalenciasId = new Hashtable<String, String>();
		fUsuarios = new File(Configuracion.get().getProperty("usuarios.nombreFichero"));
		fEquivalId = new File(Configuracion.get().getProperty("equivalenciasId.nombreFichero"));
		try {
			recuperarDatos();
		} catch (DatosException e) {
			if (e.getMessage().equals("El fichero de datos: " + fUsuarios.getName() + " o "	+ fEquivalId.getName() + " no existe...")) {	
				cargarPredeterminados();
			}
		}
	}

	/**
	 *  Método estático de acceso a la instancia única.
	 *  Si no existe la crea invocando al constructor interno.
	 *  Utiliza inicialización diferida.
	 *  Sólo se crea una vez; instancia única -patrón singleton-
	 *  @return instancia
	 */
	public static UsuariosDAO getInstancia() {
		if (instancia == null) {
			instancia = new UsuariosDAO();
		}
		return instancia;
	}

	/**
	 *  Método para generar de datos predeterminados.
	 */
	private void cargarPredeterminados() {
		try {
			String nombreUsr = Configuracion.get().getProperty("usuario.admin");
			String password = Configuracion.get().getProperty("usuario.passwordPredeterminada");	
			Usuario usrPredeterminado = new Usuario(new Nif("00000000T"), nombreUsr, "Admin Admin", 
					new DireccionPostal("Iglesia", "0", "30012", "Murcia"), 
					new Correo("jv.admin" + "@gmail.com"), new Fecha(), 
					new Fecha(), new ClaveAcceso(password), RolUsuario.ADMINISTRADOR);
			datosUsuarios.add(usrPredeterminado);
			registrarEquivalenciaId(usrPredeterminado);

			nombreUsr = Configuracion.get().getProperty("usuario.invitado");
			password = Configuracion.get().getProperty("usuario.passwordPredeterminada");	
			usrPredeterminado = new Usuario(new Nif("00000001R"), nombreUsr, "Invitado Invitado", 
					new DireccionPostal("Iglesia", "00", "30012", "Murcia"), 
					new Correo("jv.invitado" + "@gmail.com"), new Fecha(), 
					new Fecha(), new ClaveAcceso(password), RolUsuario.INVITADO);
			datosUsuarios.add(usrPredeterminado);
			registrarEquivalenciaId(usrPredeterminado);
		} 
		catch (ModeloException e) { 
			e.printStackTrace();
		}
		guardarDatos(datosUsuarios);
		guardarDatos(equivalenciasId);
	}
	
	//OPERACIONES DE PERSISTENCIA.
	/**
	 *  Recupera el Arraylist usuarios almacenados en fichero. 
	 * @throws DatosException 
	 */ 
	@Override
	public void recuperarDatos() throws DatosException {
		try {
			if (fUsuarios.exists()) {
				FileInputStream fisUsuarios = new FileInputStream(fUsuarios);
				FileInputStream fisEquivalId = new FileInputStream(fEquivalId);
				ObjectInputStream oisUsuarios = new ObjectInputStream(fisUsuarios);
				ObjectInputStream oisEquival = new ObjectInputStream(fisEquivalId);
				datosUsuarios = (ArrayList<Usuario>) oisUsuarios.readObject();
				equivalenciasId = (Hashtable<String,String>) oisEquival.readObject();	
				oisUsuarios.close();
				oisEquival.close();
				return;
			}
			throw new DatosException("El fichero de datos: " + fUsuarios.getName() + " o "	+ fEquivalId.getName() + " no existe...");	
		} 
		catch (ClassNotFoundException | IOException e)  {
			e.printStackTrace();
		}
	}

	/**
	 *  Cierra datos.
	 */
	@Override
	public void cerrar() {
		guardarDatos();
	}
	
	/**
	 *  Guarda el Arraylist de usuarios y el Hashtable de equivalencias de idUsr en ficheros.
	 */
	@Override
	public void guardarDatos() {
		guardarDatos(datosUsuarios);
		guardarDatos(equivalenciasId);
	} 
	
	/**
	 *  Guarda la lista recibida en el fichero de datos.
	 */
	private void guardarDatos(List<Usuario> listaUsuarios) {
		try {
			FileOutputStream fosUsaurios = new FileOutputStream(fUsuarios);
			ObjectOutputStream oosUsuarios = new ObjectOutputStream(fosUsaurios);
			oosUsuarios.writeObject(datosUsuarios);
			oosUsuarios.flush();
			oosUsuarios.close();
		} 
		catch (IOException e) { }
	}
	
	/**
	 *  Guarda la lista recibida en el fichero de datos.
	 */
	private void guardarDatos(Map<String,String> MapaEquivalencias) {
		try {
			FileOutputStream fosEquivalId = new FileOutputStream(fEquivalId);
			ObjectOutputStream oosEquivalId = new ObjectOutputStream(fosEquivalId);
			oosEquivalId.writeObject(equivalenciasId);
			oosEquivalId.flush();
			oosEquivalId.close();
		} 
		catch (IOException e) { }
	}
	
	//OPERACIONES DAO
		/**
		 * Búsqueda de usuario dado su idUsr, el correo o su nif.
		 * @param id - el id de Usuario a buscar.
		 * @return - el Usuario encontrado o null si no existe.
		 */
		@Override
		public Usuario obtener(String id) {
			String idUsr = equivalenciasId.get(id);
			if (idUsr != null) {
				int posicion = obtenerPosicion(idUsr);			// En base 1
				if (posicion > 0) {
					return datosUsuarios.get(posicion - 1);     // En base 0
				}
			}
			return null;				
		}

		/**
		 *  Obtiene por búsqueda binaria, la posición que ocupa, o ocuparía,  un usuario en 
		 *  la estructura.
		 *	@param IdUsr - id de Usuario a buscar.
		 *	@return - la posición, en base 1, que ocupa un objeto o la que ocuparía (negativo).
		 */
		private int obtenerPosicion(String idUsr) {
			int comparacion;
			int inicio = 0;
			int fin = datosUsuarios.size() - 1;
			int medio = 0;
			while (inicio <= fin) {
				medio = (inicio + fin) / 2;			// Calcula posición central.
				// Obtiene > 0 si idUsr va después que medio.
				comparacion = idUsr.compareTo(datosUsuarios.get(medio).getIdUsr());
				if (comparacion == 0) {			
					return medio + 1;   			// Posción ocupada, base 1	  
				}		
				if (comparacion > 0) {
					inicio = medio + 1;
				}			
				else {
					fin = medio - 1;
				}
			}	
			return -(inicio + 1);					// Posición que ocuparía -negativo- base 1
		}

		/**
		 * Búsqueda de Usuario dado un objeto, reenvía al método que utiliza idUsr.
		 * @param obj - el Usuario a buscar.
		 * @return - el Usuario encontrado; null si no existe.
		 */
		@Override
		public Usuario obtener(Object obj)  {
			return this.obtener(((Usuario) obj).getIdUsr());
		}	

		/**
		 *  Alta de un nuevo usuario en orden y sin repeticiones según el campo idUsr. 
		 *  Localiza previamente la posición de inserción, en orden, que le corresponde.
		 *  Si hay coincidencia de identificador hace 23 intentos de variar la última letra
		 *  procedente del NIF. Llama al generarVarianteIdUsr() de la clase Usuario.
		 *	@param obj - Objeto a almacenar.
		 *  @throws DatosException - si ya existe.
		 */
		@Override
		public void alta(Object obj) throws DatosException {
			assert obj != null : "Usuario null...";
			Usuario usrNuevo = (Usuario) obj;										// Para conversión cast
			int posicionInsercion = obtenerPosicion(usrNuevo.getIdUsr()); 
			if (posicionInsercion < 0) {
				datosUsuarios.add(-posicionInsercion - 1, usrNuevo); 				// Inserta el usuario en orden.
				registrarEquivalenciaId(usrNuevo);
			}
			else {
				// Hay coincidencia de identificador con un usuario ya almacenado.
				Usuario usrPrevio = datosUsuarios.get(posicionInsercion-1);
				// Comprueba que no haya coincidencia de Correo y Nif
				boolean condicion = !(usrNuevo.getCorreo().equals(usrPrevio.getCorreo())
						|| usrNuevo.getNif().equals(usrPrevio.getNif()));
				if (condicion) {
					int intentos = "ABCDEFGHJKLMNPQRSTUVWXYZ".length();				// 24 letras
					do {
						usrNuevo.generarVarianteIdUsr();
						posicionInsercion = obtenerPosicion(usrNuevo.getIdUsr());
						if (posicionInsercion < 0) {
							datosUsuarios.add(-posicionInsercion - 1, usrNuevo); 	// Inserta el usuario en orden.
							registrarEquivalenciaId(usrNuevo);
							return;
						}
						intentos--;
					} while (intentos > 0);
				}
				throw new DatosException("(ALTA) El Usuario: " + usrNuevo.getIdUsr() + " ya existe...");
			}		
		}

		/**
		 *  Añade nif y correo como equivalencias de idUsr para el inicio de sesión. 
		 *	@param usr - Usuario a registrar equivalencias. 
		 */
		private void registrarEquivalenciaId(Usuario usr) {
			assert usr != null;
			equivalenciasId.put(usr.getIdUsr(), usr.getIdUsr());
			equivalenciasId.put(usr.getNif().getTexto(), usr.getIdUsr());
			equivalenciasId.put(usr.getCorreo().getTexto(), usr.getIdUsr());
		}

		/**
		 * Elimina el objeto, dado el id utilizado para el almacenamiento.
		 * @param idUsr - el identificador del objeto a eliminar.
		 * @return - el Objeto eliminado.
		 * @throws DatosException - si no existe.
		 */
		@Override
		public Object baja(String idUsr) throws DatosException {
			assert (idUsr != null);
			int posicion = obtenerPosicion(idUsr); 							// En base 1
			if (posicion > 0) {
				Usuario usrEliminado = datosUsuarios.remove(posicion - 1); 	// En base 0
				equivalenciasId.remove(usrEliminado.getIdUsr());
				equivalenciasId.remove(usrEliminado.getNif());
				equivalenciasId.remove(usrEliminado.getCorreo());
				return usrEliminado;
			}
			throw new DatosException("(BAJA) El Usuario: " + idUsr + " no existe...");
		} 

		/**
		 *  Actualiza datos de un Usuario reemplazando el almacenado por el recibido. 
		 *  No admitirá cambios en el idUsr.
		 *	@param obj - Usuario con los cambios.
		 *  @throws DatosException - si no existe.
		 */
		@Override
		public void actualizar(Object obj) throws DatosException {
			assert obj != null;
			Usuario usrActualizado = (Usuario) obj;									// Para conversión cast
			int posicion = obtenerPosicion(usrActualizado.getIdUsr()); 				// En base 1
			if (posicion > 0) {
				// Reemplaza equivalencias de Nif y Correo
				Usuario usrModificado = datosUsuarios.get(posicion - 1); 			// En base 0
				equivalenciasId.remove(usrModificado.getNif());
				equivalenciasId.remove(usrModificado.getCorreo());
				equivalenciasId.put(usrActualizado.getNif().getTexto(), usrActualizado.getIdUsr());
				equivalenciasId.put(usrActualizado.getCorreo().getTexto(), usrActualizado.getIdUsr());	
				// Reemplaza elemento
				datosUsuarios.set(posicion - 1, usrActualizado);  					// En base 0		
				return;
			}
			throw new DatosException("(ACTUALIZAR) El Usuario: " + usrActualizado.getIdUsr() + " no existe...");
		} 

		/**
		 * Obtiene el listado de todos los usuarios almacenados.
		 * @return el texto con el volcado de datos.
		 */
		@Override
		public String listarDatos() {
			StringBuilder listado = new StringBuilder();
			for (Usuario usuario: datosUsuarios) {
				if (usuario != null) {
					listado.append("\n" + usuario); 
				}
			}
			return listado.toString();
		}

		/**
		 * Elimina todos los usuarios almacenados y regenera los predeterminados.
		 */
		@Override
		public void borrarTodo() {
			datosUsuarios = new ArrayList<Usuario>();
			equivalenciasId = new Hashtable<String, String>();
			cargarPredeterminados();
		}
		
	} //class