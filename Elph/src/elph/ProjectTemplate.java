package elph;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ProjectTemplate extends Composite {
	private Text text_2;
	private Table table;
	private Table table_1;
	private Table table_2;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ProjectTemplate(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(3, true);
		setLayout(gridLayout);
		
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setText("Your Open Liberty Path");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Button btnEdkt = new Button(this, SWT.NONE);
		btnEdkt.setText("Edit Directory Location");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label spaceFill = new Label(this, SWT.NONE);
		spaceFill.setText("");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		text_2 = new Text(this, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Button btnFilterProjects = new Button(this, SWT.NONE);
		btnFilterProjects.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnFilterProjects.setText("Filter Projects");
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		
		Label lblAllProjects = new Label(this, SWT.NONE);
		lblAllProjects.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAllProjects.setText("All Projects");
		
		Label lblImportDeps = new Label(this, SWT.NONE);
		lblImportDeps.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblImportDeps.setText("Import Deps");
		
		Label lblDepsusers = new Label(this, SWT.NONE);
		lblDepsusers.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblDepsusers.setText("Deps+Users");
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
//		gd_table.widthHint = 50;
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		
		TableItem tableItem = new TableItem(table, SWT.NONE);
		tableItem.setText("New TableItem 123456");
		
		table_1 = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		table_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table_1.setLinesVisible(true);
		
		TableItem tableItem_1 = new TableItem(table_1, SWT.NONE);
		tableItem_1.setText("New TableItem");
		
		table_2 = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
//		table_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table_2.setLinesVisible(true);
		
		TableItem tableItem_2 = new TableItem(table_2, SWT.NONE);
		tableItem_2.setText("New TableItem");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
