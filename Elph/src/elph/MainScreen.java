package elph;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class MainScreen extends ViewPart {

	public static Table sourceTableVar = null;
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
	    
//		Label olLocation = new Label(parent, SWT.WRAP);
//		olLocation.setText("N/A");
//		olLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
		Button fileBrowser = new Button(parent, SWT.PUSH);
	    fileBrowser.setText("Edit Directory Location...");
	    fileBrowser.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.NULL);
                String path = dialog.open();
                if (path != null) {
                	olLabel.setText(olPathTemplate + path);
//                	olLocation.setText(path);
//                	olLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER));
                }
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
	    

		
		final Table projectsTable = createTable(parent, "Projects Table", 15);
		final Table depsTable = createTable(parent, "Deps Table", 15);
		final Table usersTable = createTable(parent, "Users Table", 15);




//		
//		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
//        scrolledComposite.setLayout(new FillLayout());
//
//        // Create a List
//        List list = new List(scrolledComposite, SWT.BORDER);
//        for (int i = 0; i < 50; i++) {
//            list.add("Item " + i);
//        }
//        
//        Color white = parent.getDisplay().getSystemColor(SWT.COLOR_WHITE);
//        list.setBackground(white);
//        scrolledComposite.setBackground(white);
//        list.setSize(130,600);
//        // Set the content of the ScrolledComposite
//        scrolledComposite.setContent(list);
//
//        // Set the minimum size to trigger scrolling (works without this so not sure if needed)
//        scrolledComposite.setMinSize(list.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//        
//        ScrolledComposite scrolledComposite2 = new ScrolledComposite(parent, SWT.V_SCROLL);
//        scrolledComposite2.setLayout(new FillLayout());
//
//        // Create a List
//        List list2 = new List(scrolledComposite2, SWT.BORDER);
//
//        list2.setBackground(white);
//        scrolledComposite2.setBackground(white);
//        list2.setSize(130,600);
//        // Set the content of the ScrolledComposite
//        scrolledComposite2.setContent(list2);
//        
//        
//	    list.addSelectionListener(new SelectionListener() {
//	      public void widgetSelected(SelectionEvent event) {
////	    	  System.out.println("A: " + Arrays.toString(list.getSelection()));
//	    	  System.out.println("A: " + list.getSelection()[0]);
//	    	  list2.add(list.getSelection()[0]);
//	    	  list.remove(list.getSelectionIndices()[0]);
//	      }
//
//	      public void widgetDefaultSelected(SelectionEvent event) {
////	    	  System.out.println("B: " + Arrays.toString(list.getSelection()));
//	    	  System.out.println("B: " + list.getSelection()[0]);
//	      }
//	    });
//
//        // Set the minimum size to trigger scrolling (works without this so not sure if needed)
//        scrolledComposite2.setMinSize(list.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		
		
		
		
//		// Create the first table
//		   Table table1 = createTable(parent, "List 1");
//	        Table table2 = createTable(parent, "List 2");
//
//	        // Add some sample items to the tables
//	        for (int i = 1; i <= 5; i++) {
//	            TableItem item1 = new TableItem(table1, SWT.NONE);
//	            item1.setText(new String[] { "Itemm,bkb,nnkjbjkbkjbkvhvjchjvkhvcgjgvvulcxkfclfx " + i });
//
//	            TableItem item2 = new TableItem(table2, SWT.NONE);
//	            item2.setText(new String[] { "Item " + (i + 5) });
//	        }
//	        addDragAndDropSupport(table1, table2);
//	        addDragAndDropSupport(table2, table1);
	        
//	        final Label dragLabel = new Label(parent, SWT.BORDER);
//	     	dragLabel.setText("text to be transferred");
	
	}
	
	private Table createTable(Composite parent, String title, int rows) {
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
     	      // Only start the drag if there is actually text in the
     	      // label - this text will be what is dropped on the target.
	          if (selection.length > 0) {
                if (selection[0].getText(0).length() == 0) {
	     	          event.doit = false;
	     	      }
            }
     	 
     	   }
     	   public void dragSetData(DragSourceEvent event) {
     	     // Provide the data of the requested type.
     	     if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
		            TableItem[] selection = sourceTable.getSelection();
		            if (selection.length > 0) {
		                event.data = selection[0].getText(0) + "0987654321098765432109876543210987654321§0987654321§";
		                sourceTableVar = sourceTable;
		            }
     	     }
     	   }
     	   public void dragFinished(DragSourceEvent event) {
     	     // If a move operation has been performed, remove the data
     	     // from the source
     	     if (event.detail == DND.DROP_MOVE) {
     	    	 System.out.println("Remove from current table"); 
     	    	sourceTable.remove(sourceTable.getSelectionIndices()); 
     	     }
     	     }
     	   });
	}
	private void addDropSupport(Table targetTable) {
		// Allow data to be copied or moved to the drop target
	 	int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
	 	DropTarget target = new DropTarget(targetTable, operations);
	 	 
	 	// Receive data in Text or File format
	 	final TextTransfer textTransfer = TextTransfer.getInstance();
	 	final FileTransfer fileTransfer = FileTransfer.getInstance();
	 	Transfer[] types = new Transfer[] {fileTransfer, textTransfer};
	 	target.setTransfer(types);
	 	 
	 	target.addDropListener(new DropTargetListener() {
	 	  public void dragEnter(DropTargetEvent event) {
	 		
	 	     if (event.detail == DND.DROP_DEFAULT) {
	 	    	 //checking whether the DROP_MOVE operation is supported in the current drag-and-drop context
	 	         if ((event.operations & DND.DROP_MOVE) != 0) {
	 	             event.detail = DND.DROP_MOVE;
	 	         } else {
	 	             event.detail = DND.DROP_NONE;
	 	         }
	 	         
//	 	        if ((event.operations & DND.DROP_MOVE) != 0) {
//	 	        	   if (textTransfer.isSupportedType(event.currentDataType)) {
//	 		 	            String text = (String)event.data;
//	 		 	            if (containsItemWithText(tagetTable, text)) {
//	 		 	            	event.detail = DND.DROP_NONE;
//	 		 	            } else {
//	 		 	            	event.detail = DND.DROP_MOVE;
//	 		 	            }
//	 	        	   }
//	 	         } else {
//	 	             event.detail = DND.DROP_NONE;
//	 	         }
	 	     }
	 	     // will accept text but prefer to have files dropped
	 	     boolean isCompliant = false;
	 	     for (int i = 0; i < event.dataTypes.length; i++) {
	 	         if (fileTransfer.isSupportedType(event.dataTypes[i])){
	 	        	 isCompliant=true;
	 	             event.currentDataType = event.dataTypes[i];
	 	             // files should only be copied
	 	             if (event.detail != DND.DROP_MOVE) {
	 	                 event.detail = DND.DROP_NONE;
	 	             }
	 	             break;
	 	         }
	 	     }

	 	     if (isCompliant) System.out.println("Compliant: " + event.currentDataType);
	 	     else System.out.println("Not Compliant: " + event.currentDataType);
		         
	 	   }
	 	   public void dragOver(DropTargetEvent event) {
	 	        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
	 	        if (textTransfer.isSupportedType(event.currentDataType)) {
	 	            // NOTE: on unsupported platforms this will return null
	 	            Object o = textTransfer.nativeToJava(event.currentDataType);
	 	            String t = (String)o;
	 	            if (t != null) System.out.println(t);
//	 	           System.out.println("Drag Over Event Data = " + (String)event.data);
	 	        }

	 	    }
	 	    public void dragOperationChanged(DropTargetEvent event) {
	 	    	System.out.println("Drag OperationChanged Event Data = " + (String)event.data);
	 	        if (event.detail == DND.DROP_DEFAULT) {
	 	        	 
	 	            if ((event.operations & DND.DROP_MOVE) != 0) {
	 	                event.detail = DND.DROP_MOVE;
	 	            } else {
	 	                event.detail = DND.DROP_NONE;
	 	            }
	 	        }
	 	        // allow text to be moved but files should only be copied
	 	        if (fileTransfer.isSupportedType(event.currentDataType)){
	 	            if (event.detail != DND.DROP_MOVE) {
	 	                event.detail = DND.DROP_NONE;
	 	            }
	 	        }
	 	    }
	 	    public void dragLeave(DropTargetEvent event) {
	 	    }
	 	    public void dropAccept(DropTargetEvent event) {
	 	    }
	 	    public void drop(DropTargetEvent event) {
		 		  System.out.println("Drag Enter - Source Table = " + Objects.hash(sourceTableVar));
			 		 System.out.println("Drag Enter - Target Table = " + Objects.hash(targetTable));
	 	        if (textTransfer.isSupportedType(event.currentDataType)) {
	 	        	if (sourceTableVar != targetTable) {
	 	        		String text = (String)event.data;
	 	            	 TableItem item = new TableItem(targetTable, SWT.NONE);
	 	 	            item.setText(text);
	 	        	} else event.detail = DND.DROP_NONE;
	 	        }
	 	        if (fileTransfer.isSupportedType(event.currentDataType)){
	 	            String[] files = (String[])event.data;
	 	            for (int i = 0; i < files.length; i++) {
	 	            	if (sourceTableVar != targetTable) {
	 	                   TableItem item = new TableItem(targetTable, SWT.NONE);
		 	                item.setText(files[i]);
	 	            	} else {
	 	            		 event.detail = DND.DROP_NONE;
	 	            	}
	 	            }
	 	        }
	 	    }
	 	});
	}

    private static boolean containsItemWithText(Table table, String searchText) {
        for (TableItem item : table.getItems()) {
            if (searchText.equals(item.getText())) {
                return true; // Found a match
            }
        }
        return false; // No match found
    }
//	  private Table createTable(Composite parent, String title) {
//		    Table table = new Table(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
//		    table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		    table.setHeaderVisible(true);
//		    table.setLinesVisible(true);
//
//		    // Create a single column
//		    TableColumn column = new TableColumn(table, SWT.NONE);
//		    column.setText(title);
//		    column.setWidth(200); // Set an appropriate width for the column
//
//		    // Dispose of any additional columns
//		    for (TableColumn additionalColumn : table.getColumns()) {
//		        if (additionalColumn != column) {
//		            additionalColumn.dispose();
//		        }
//		    }
//		    return table;
//	    }
//
//	  private void addDragAndDropSupport(Table sourceTable, Table targetTable) {
//		    DragSource dragSource = new DragSource(sourceTable, DND.DROP_MOVE);
//		    dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance() });
//
//		    dragSource.addDragListener(new DragSourceAdapter() {
//		        @Override
//		        public void dragSetData(DragSourceEvent event) {
//		            TableItem[] selection = sourceTable.getSelection();
//		            if (selection.length > 0) {
//		                event.data = selection[0].getText(0);
//		            }
//		        }
//		    });
//
//		    DropTarget dropTarget = new DropTarget(targetTable, DND.DROP_MOVE);
//		    dropTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
//
//		    dropTarget.addDropListener(new DropTargetAdapter() {
//		        @Override
//		        public void drop(DropTargetEvent event) {
//		            if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
//		                String data = (String) event.data;
//
//		                // Create a new item in the target table
//		                TableItem item = new TableItem(targetTable, SWT.NONE);
//		                item.setText(0, data);
//
//		                // Delete the dragged item from the source table
//		                TableItem[] selection = sourceTable.getSelection();
//		                if (selection.length > 0) {
//		                    selection[0].dispose();
//		                }
//		            }
//		        }
//		    });
//		}


	@Override
	public void setFocus() {
		/**
		 *     Purpose: This method is called when the view receives focus, 
		 *     			and it should set the focus on the primary control or widget within the view.
		 *     Implementation: Use this method to set the focus on the appropriate UI element within your view.
		 */
		
	}
	
	  public static void setDragDrop(final Label label) {

		    Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		    int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

		    final DragSource source = new DragSource(label, operations);
		    source.setTransfer(types);
		    source.addDragListener(new DragSourceListener() {
		      public void dragStart(DragSourceEvent event) {
		        event.doit = (label.getText().length() != 0);
		      }

		      public void dragSetData(DragSourceEvent event) {
		        event.data = label.getText();
		      }

		      public void dragFinished(DragSourceEvent event) {
		        if (event.detail == DND.DROP_MOVE)
		          label.setText("");
		      }
		    });

		    DropTarget target = new DropTarget(label, operations);
		    target.setTransfer(types);
		    target.addDropListener(new DropTargetAdapter() {
		      public void drop(DropTargetEvent event) {
		        if (event.data == null) {
		          event.detail = DND.DROP_NONE;
		          return;
		        }
		        label.setText((String) event.data);
		      }
		    });
		  }
	
	public String getTitle() { return "Eclipse Liberty Project Helper"; }
	
	public void errorDialogue(Composite parent, String errMsg) { 
		MessageDialog.openError(
				parent.getShell(), 
				getTitle(), 
				errMsg);
	}
	
	public void infoDialogue(Composite parent, String msg) { 
		MessageDialog.openInformation(
				parent.getShell(), 
				getTitle(), 
				msg);
	}
	
	public boolean validatePath(String path) { 
		System.out.println("PATH = " + path);
		return path.length() > 3 && path.endsWith("/dev"); }
	
	private static void importProject(final File baseDirectory, final String projectName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
				new Path(baseDirectory.getAbsolutePath() + "/.project"));
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
		project.create(description, null);
		project.open(null);
	}
	
}
