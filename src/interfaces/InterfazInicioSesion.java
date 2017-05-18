package interfaces;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InterfazInicioSesion {

	private static JFrame frame;
	private JTextField textUsuario;
	private JPasswordField passwordField;
	
	/**	
	 * Launch the application.
	*/
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazInicioSesion window = new InterfazInicioSesion();
					window.frame.setVisible(true);
					//Pone la ventana en la posicion central
					frame.setLocationRelativeTo(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfazInicioSesion() {
		initialize();
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 443, 449);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblInicioDeSesin = new JLabel("INICIO DE SESIÓN");
		lblInicioDeSesin.setBackground(Color.BLACK);
		lblInicioDeSesin.setFont(new Font("Tahoma", Font.PLAIN, 29));
		lblInicioDeSesin.setBounds(87, 58, 248, 48);
		frame.getContentPane().add(lblInicioDeSesin);
		
		JLabel lblUsuario = new JLabel("Usuario:");
		lblUsuario.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblUsuario.setBounds(63, 174, 76, 16);
		frame.getContentPane().add(lblUsuario);
		
		JLabel lblContrasea = new JLabel("Contraseña:");
		lblContrasea.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblContrasea.setBounds(34, 231, 105, 16);
		frame.getContentPane().add(lblContrasea);
		
		textUsuario = new JTextField();
		textUsuario.setFont(new Font("Tahoma", Font.BOLD, 16));
		textUsuario.setBounds(148, 172, 195, 22);
		frame.getContentPane().add(textUsuario);
		textUsuario.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Tahoma", Font.BOLD, 16));
		passwordField.setBounds(151, 229, 192, 22);
		frame.getContentPane().add(passwordField);
		
		JButton btnIniciar = new JButton("INICIAR");
		btnIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String usuario="admin";
				String contraseña="123";
				
				String contraseñaIntr=new String(passwordField.getPassword());
				String usuarioIntr= new String(textUsuario.getText());
				
				if (contraseñaIntr.equals(contraseña) && usuarioIntr.equals(usuario)){
					System.out.println("bienvenido");
				}else{
					
				}
					
			}
		});
		btnIniciar.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnIniciar.setBounds(34, 336, 110, 25);
		frame.getContentPane().add(btnIniciar);
	}

	


	
	
}
