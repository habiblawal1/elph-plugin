package elph;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class MainScreen extends ViewPart {

	public static Table sourceTableVar = null;
	public static boolean draggedToDropTarget = false;
	
	@Override
	public void createPartControl(Composite parent) {
		/**
		 *     Purpose: This method is responsible for creating the content of the view.
		 *     Parameters:
		 *     		parent: The parent composite (container widget that can hold other widgets) in which the view content should be created.
		 *     Implementation: You typically create SWT controls (widgets) within the provided Composite to define the appearance
		 *     				   and behaviour of your view.
		 */
		
		parent.setLayout(new GridLayout(3, false));
		
		String olPathTemplate = "Your Open Liberty path: ";
		Label olLabel = new Label(parent, SWT.NONE);
		olLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		olLabel.setText(olPathTemplate + "N/A");
	    
		Button fileBrowser = new Button(parent, SWT.PUSH);
	    fileBrowser.setText("Edit Directory Location...");
	    fileBrowser.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.NULL);
                String path = dialog.open();
                if (path != null) olLabel.setText(olPathTemplate + path);
            }
        });
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		
	    //Add some blank rows for spacing
	    for (int i=0; i<3; i++) new Label(parent, SWT.NONE);
	    
	    Label importLabel = new Label(parent, SWT.WRAP);
	    importLabel.setText("Please type below the project you would like to import");
		importLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 3, 1));
		
		Text projectFilterText = new Text(parent, SWT.BORDER);
		projectFilterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		Button importBtn = new Button(parent, SWT.PUSH);
		importBtn.setText("Import Project");
		importBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Check OL dev directory is selected (its the label without the "Your Open Liberty path: " string
				if (validatePath(olLabel.getText().substring(olPathTemplate.length()))) {
//					/Users/habiblawal/Documents/GitHub/open-liberty/dev/cnf
//					/Users/habiblawal/Documents/GitHub/open-liberty/dev/build.sharedResources
//					/Users/habiblawal/Documents/GitHub/open-liberty/dev/com.ibm.ws.jndi.open_fat
					String projectPath = projectFilterText.getText();
					File file = new File(projectPath);
					try {
						if (file.exists() && file.isDirectory()) {
							importProject(file, file.getName());
							infoDialogue(parent, "Successfully Imported " + file.getName());
							projectFilterText.setText("");
						} else throw new RuntimeException("Failed to import project. Please double check the project exists");
					} catch (RuntimeException | CoreException  err) {
						errorDialogue(parent, err.getMessage());
					}
				} else errorDialogue(parent, "Please choose a valid directory location of your Open Liberty location. "
					+ "You must choose the 'dev' folder.");
			}	
		});
		new Label(parent, SWT.NONE);

		
		Button btnFilterProjects = new Button(parent, SWT.NONE);
		btnFilterProjects.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnFilterProjects.setText("Filter Projects");
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		
		Label lblAllProjects = new Label(parent, SWT.NONE);
		lblAllProjects.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAllProjects.setText("All Projects");
		
		Label lblImportDeps = new Label(parent, SWT.NONE);
		lblImportDeps.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblImportDeps.setText("Import Deps");
		
		Label lblDepsusers = new Label(parent, SWT.NONE);
		lblDepsusers.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblDepsusers.setText("Deps+Users");
	    

		final Table projectsTable = createTable(parent, 15);
		final Table depsTable = createTable(parent, 10);
		final Table usersTable = createTable(parent, 5);
	}
	
	
	@Override
	public void setFocus() {
		/**
		 *     Purpose: This method is called when the view receives focus, 
		 *     			and it should set the focus on the primary control or widget within the view.
		 *     Implementation: Use this method to set the focus on the appropriate UI element within your view.
		 */
		
	}
	
	
	public String getTitle() { return "Eclipse Liberty Project Helper"; }
	
	
	private boolean validatePath(String path) { return path.length() > 3 && path.endsWith("/dev"); }
	
	
	private static void importProject(final File baseDirectory, final String projectName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
				new Path(baseDirectory.getAbsolutePath() + "/.project"));
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
		project.create(description, null);
		project.open(null);
	}
	
	
	private void errorDialogue(Composite parent, String errMsg) { 
		MessageDialog.openError(
				parent.getShell(), 
				getTitle(), 
				errMsg);
	}
	
	
	private void infoDialogue(Composite parent, String msg) { 
		MessageDialog.openInformation(
				parent.getShell(), 
				getTitle(), 
				msg);
	}
	
	
	private Table createTable(Composite parent, int rows) {
		Table table = new Table(parent, SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
//	    table.setHeaderVisible(true);
//	    TableColumn column1 = new TableColumn(table, SWT.NONE);
//		column1.setText("Name");
//		
		for (int i = 0; i < rows; i++) {
		    TableItem item = new TableItem(table, SWT.NONE);
		    item.setText("item" + i);
		}
		addDragSupport(table);
		addDropSupport(table);
		return table;
	}
	
	
	private void addDragSupport(Table sourceTable) {
		// Allow data to be copied or moved from the drag source
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
     	DragSource source = new DragSource(sourceTable, operations);
     	 
     	// Provide data in Text format
     	Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
     	source.setTransfer(types);
     	 
     	source.addDragListener(new DragSourceListener() {
     		TableItem[] selection = sourceTable.getSelection();
     		
     		public void dragStart(DragSourceEvent event) {
     			sourceTableVar = sourceTable;
     			//Reset bool which will be used to check if an item has been dropped to another valid drop target
     			draggedToDropTarget = false;
     			
     			// Only start the drag if there is actually text in the label
     			//This text will be what is dropped on the target.
     			if (selection.length > 0) {
     				if (selection[0].getText(0).length() == 0) event.doit = false;
     			}
     		}
     		
     		public void dragSetData(DragSourceEvent event) {
     			// Provide the data of the requested type.
     			if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
     				TableItem[] selection = sourceTable.getSelection();
     				if (selection.length > 0) event.data = selection[0].getText(0);
     			}
     		}
     		
     		public void dragFinished(DragSourceEvent event) {
     			// If data has been moved to another valid drop target, remove the data from the source 
     			if (event.detail == DND.DROP_MOVE && draggedToDropTarget) sourceTable.remove(sourceTable.getSelectionIndices()); 
     		}
     	});
	}
	
	
	private void addDropSupport(Table targetTable) {
		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
	 	DropTarget target = new DropTarget(targetTable, operations);
	 	 
	 	// Receive data in Text format
	 	final TextTransfer textTransfer = TextTransfer.getInstance();
	 	Transfer[] types = new Transfer[] {textTransfer};
	 	target.setTransfer(types);
	 	 
	 	target.addDropListener(new DropTargetListener() {
	 		public void dragEnter(DropTargetEvent event) {
	 			if (event.detail == DND.DROP_DEFAULT) {
	 				//checking whether the DROP_MOVE operation is supported in the current drag-and-drop context
	 				//and you are not dragging an item to the same table it came from
	 				if ((event.operations & DND.DROP_MOVE) != 0 && sourceTableVar != targetTable) event.detail = DND.DROP_MOVE;
	 				else event.detail = DND.DROP_NONE;
	 			}
		    }
	 		
	 		public void dragOver(DropTargetEvent event) {
	 			event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
	 	        if (textTransfer.isSupportedType(event.currentDataType)) {
	 	        	// NOTE: on unsupported platforms this will return null
	 	            Object o = textTransfer.nativeToJava(event.currentDataType);
	 	            String t = (String)o;
	 	            if (t != null) System.out.println(t);
	 	        }
	 	    }
	 		
	 	    public void dragOperationChanged(DropTargetEvent event) {
	 	    	if (event.detail == DND.DROP_DEFAULT) {
	 	    		if ((event.operations & DND.DROP_MOVE) != 0) event.detail = DND.DROP_MOVE;
	 	            else event.detail = DND.DROP_NONE;
	 	        }
	 	    }
	 	    
	 	    public void dragLeave(DropTargetEvent event) {}
	 	    
	 	    public void dropAccept(DropTargetEvent event) {}
	 	    
	 	    public void drop(DropTargetEvent event) {
	 	    	if (textTransfer.isSupportedType(event.currentDataType)) {
	 	    		String text = (String)event.data;
	 	        	if (sourceTableVar != targetTable && !checkTableItemDuplicate(targetTable, text)) {
	 	        		draggedToDropTarget = true;
	 	        		TableItem item = new TableItem(targetTable, SWT.NONE);
	 	        		item.setText(text);
	 	        	} else event.detail = DND.DROP_COPY;
	 	        }
	 	    }
	 	});
	}
	
	
	private static boolean checkTableItemDuplicate(Table table, String searchText) {
		for (TableItem item : table.getItems()) {
			if (searchText.equals(item.getText())) return true; // Found a match
		}
		return false; // No match found
	}
}
