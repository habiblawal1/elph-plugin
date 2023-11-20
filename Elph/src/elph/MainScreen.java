package elph;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
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
//	    browse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,1,0));
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
	    
		Button importBtn = new Button(parent, SWT.PUSH);
		importBtn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,2,1));
		importBtn.setText("Import Project");
		importBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (validatePath(olLocation.getText())) infoDialogue(parent, "Successfully Imported Projects!");
				else errorDialogue(parent, "Please choose a valid directory location of your Open Liberty location. "
						+ "You must choose the 'dev' repository.");
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
}
