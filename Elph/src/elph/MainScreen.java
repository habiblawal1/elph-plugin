package elph;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class MainScreen extends ViewPart {

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
		
		Label olLabel = new Label(parent, SWT.NONE);
		olLabel.setText("Your Open Liberty path: ");
	    
		Label olLocation = new Label(parent, SWT.WRAP);
		olLocation.setText("N/A");
		olLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	    
		Button fileBrowser = new Button(parent, SWT.PUSH);
	    fileBrowser.setText("Edit Directory Location...");
	    fileBrowser.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.NULL);
                String path = dialog.open();
                if (path != null) {
                	olLocation.setText(path);
                	olLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER));
                }
            }
        });
	    
	    //Add some blank rows for spacing
	    for (int i=0; i<3; i++) {
	    	new Label(parent, SWT.NONE);
	    }
	    
	    Label importLabel = new Label(parent, SWT.WRAP);
	    importLabel.setText("Please type below the project you would like to import");
		importLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 3, 1));
	    
	    Text importTextBox = new Text(parent, SWT.BORDER);
	    importTextBox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,2,1));
	    
		Button importBtn = new Button(parent, SWT.PUSH);
		importBtn.setText("Import Project");
		importBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (validatePath(olLocation.getText())) {
//					/Users/habiblawal/Documents/GitHub/open-liberty/dev/cnf
//					/Users/habiblawal/Documents/GitHub/open-liberty/dev/build.sharedResources
//					/Users/habiblawal/Documents/GitHub/open-liberty/dev/com.ibm.ws.jndi.open_fat
					String projectPath = importTextBox.getText();
					File file = new File(projectPath);
					try {
						if (file.exists() && file.isDirectory()) {
							importProject(file, file.getName());
							infoDialogue(parent, "Successfully Imported " + file.getName());
							importTextBox.setText("");
						} else throw new RuntimeException("Failed to import project. Please double check the project exists");
					} catch (RuntimeException | CoreException  err) {
						errorDialogue(parent, err.getMessage());
					}
				} else errorDialogue(parent, "Please choose a valid directory location of your Open Liberty location. "
					+ "You must choose the 'dev' folder.");
			}	
		});
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
	
	public boolean validatePath(String path) { return path.length() > 3 && path.endsWith("/dev"); }
	
	private static void importProject(final File baseDirectory, final String projectName) throws CoreException {
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
				new Path(baseDirectory.getAbsolutePath() + "/.project"));
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
		project.create(description, null);
		project.open(null);
	}
	
}
