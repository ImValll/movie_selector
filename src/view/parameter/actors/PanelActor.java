package view.parameter.actors;

import model.ButtonEditor;
import model.ButtonRenderer;
import model.parameter.actors.GestionnaireActor;
import model.parameter.actors.Actor;
import model.parameter.actors.ActorTableModel;
import view.parameter.ParameterFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Objects;

public class PanelActor extends JPanel {

	ParameterFrame actorFrame;

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel cards = new JPanel(cardLayout); // Panel that uses CardLayout
	private JTable tableArea; // Display book information
	private ActorTableModel tableModel;
	GestionnaireActor gestionnaireActor;
	private JTextField searchTitleField;

	public PanelActor(GestionnaireActor gestionnaireActor, ParameterFrame actorFrame) {
		this.gestionnaireActor = gestionnaireActor;
		this.actorFrame = actorFrame;
		initializeUI();
	}

	private void initializeUI() {
		setLayout(new BorderLayout());
		add(cards, BorderLayout.CENTER);

		JPanel actorGrid = createActorPanel();

		add(actorGrid);
	}

	private JPanel createActorPanel() {
		JPanel panel = new JPanel(new BorderLayout());

		// Top panel for search
		JPanel topPanel = new JPanel();
		searchTitleField = new JTextField(20);

		JButton searchButton = ButtonEditor.createButton("Rechercher un acteur", new Color(70, 130, 180));
		searchButton.addActionListener(this::searchActor);

		JButton addActorButton = ButtonEditor.createButton("Ajouter un acteur", new Color(70, 130, 180));

		JLabel titleLabel = new JLabel("Nom : ");
		titleLabel.setForeground(Color.WHITE);
		topPanel.add(titleLabel);

		topPanel.add(searchTitleField);
		topPanel.add(searchButton);
		topPanel.add(addActorButton);

		addActorButton.addActionListener(e -> addActor());

		// Get JScrollPane from showAllActor method
		JScrollPane scrollPane = showAllActor();

		topPanel.setBackground(new Color(50, 50, 50));
		scrollPane.setBackground(new Color(50, 50, 50));
		panel.add(topPanel, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		JButton btnBack = ButtonEditor.createButton("Retour", new Color(70, 130, 180));
		btnBack.addActionListener(e -> backMenu());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(new Color(50, 50, 50));
		bottomPanel.add(btnBack);
		panel.add(bottomPanel, BorderLayout.SOUTH);

		return panel;
	}

	private JScrollPane showAllActor() {
		java.util.List<Actor> listActor = gestionnaireActor.getActor();
		tableModel = new ActorTableModel(listActor);
		tableArea = new JTable(tableModel);
		tableArea.setBackground(Color.LIGHT_GRAY);

		// Ajout de rendus personnalisés pour les colonnes "Modifier" et "Supprimer"
		tableArea.getColumn("Modifier").setCellRenderer(new ButtonRenderer());
		tableArea.getColumn("Modifier").setCellEditor(new ButtonEditor(this));

		tableArea.getColumn("Supprimer").setCellRenderer(new ButtonRenderer());
		tableArea.getColumn("Supprimer").setCellEditor(new ButtonEditor(this));

		return new JScrollPane(tableArea);
	}

	private void searchActor(ActionEvent e) {
		String titre = searchTitleField.getText();
		java.util.List<Actor> result = gestionnaireActor.searchActor(titre);
		if (result.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Erreur: Aucun acteur trouvé pour ce nom.", "Erreur", JOptionPane.ERROR_MESSAGE);
		} else {
			tableModel.setActor(result); // Mettre à jour le modèle du tableau
		}
	}

	private void addActor() {
		JTextField titleField = new JTextField();

		final JComponent[] inputs = new JComponent[] {
				new JLabel("Nom*"),
				titleField,
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Ajouter un nouvel acteur", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			String titre = titleField.getText();

			if(titre.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Erreur: Le nom doit être entré.", "Erreur nom vide", JOptionPane.ERROR_MESSAGE);
			}
			else {

				boolean canBeAdd = true;

				java.util.List<Actor> listActor = gestionnaireActor.getActor();
				for (Actor actor : listActor) {
					if (actor.getName().equalsIgnoreCase(titre)) {
						canBeAdd = false;
						JOptionPane.showMessageDialog(this, "Erreur: L'acteur " + titre + " a déjà été ajouté", "Erreur doublons", JOptionPane.ERROR_MESSAGE);
						break;
					}
				}

				if (canBeAdd) {
					Actor newActor = new Actor(titre);
					gestionnaireActor.addActor(newActor); // Ajouter l'acteur à votre gestionnaire d'acteur
					JOptionPane.showMessageDialog(this, "Acteur ajouté avec succès: " + titre, "Acteur Ajouté", JOptionPane.INFORMATION_MESSAGE);
				}
			}

			// Mettre à jour le modèle de tableau après l'ajout de l'acteur
			java.util.List<Actor> updatedActor = gestionnaireActor.getActor(); // Récupérer la liste mise à jour des acteurs
			tableModel.setActor(updatedActor); // Mettre à jour le modèle du tableau
		}
	}

	public void backMenu() {
		actorFrame.dispose();
		new ParameterFrame();
	}

	public void deleteActor(Actor actor) {
		gestionnaireActor.deleteActor(actor);
		afficheMessage();

		java.util.List<Actor> updatedActor = gestionnaireActor.getActor(); // Récupérer la liste mise à jour des acteurs
		tableModel.setActor(updatedActor); // Mettre à jour le modèle du tableau
	}

	public void editActor(Actor actor) {

		JTextField oldTitleField = new JTextField(actor.getName());
		oldTitleField.setEnabled(false);

		JTextField titleField = new JTextField(actor.getName());



		final JComponent[] inputs = new JComponent[] {
				new JLabel("Ancien nom"),
				oldTitleField,
				new JLabel("Nom*"),
				titleField,
		};

		int result = JOptionPane.showConfirmDialog(this, inputs, "Modifier un acteur", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			String titre = titleField.getText();

			if(titre.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Erreur: Le titre doit être entré.", "Erreur titre vide", JOptionPane.ERROR_MESSAGE);
			} else {
				Actor newActor = new Actor(titre);
				gestionnaireActor.editActor(oldTitleField.getText(), newActor); // Ajouter l'acteur à votre gestionnaire d'acteur
			}

			// Mettre à jour le modèle de tableau après l'ajout de l'acteur
			List<Actor> updatedActor = gestionnaireActor.getActor(); // Récupérer la liste mise à jour des acteurs
			tableModel.setActor(updatedActor); // Mettre à jour le modèle du tableau

			afficheMessage();
		}

	}

	public JTable getTableArea() {
		return tableArea;
	}

	public GestionnaireActor getGestionnaire() {
		return gestionnaireActor;
	}

	public void afficheMessage() {
		String[] message = gestionnaireActor.getMessage();

		if(message[0] != null) {
			if(Objects.equals(message[0], "e")) {
				JOptionPane.showMessageDialog(this, message[2], message[1], JOptionPane.ERROR_MESSAGE);
			} else if(Objects.equals(message[0], "i")) {
				JOptionPane.showMessageDialog(this, message[2], message[1], JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
}
