/// <summary>
/// Interaction logic for CDDDetailView.xaml
/// </summary>
public partial class CDDDetailView : UserControl
{
	public CDDDetailView()
	{
		InitializeComponent();
	}

	private void AuxView_Loaded(object sender, RoutedEventArgs e)
	{
		if (DataContext is CDDViewModel)
		{
			if (((CDDViewModel)DataContext).IsCDDReadOnly())
				if (sender is MitigatedNumericBox)
					(sender as MitigatedNumericBox).IsReadOnly = true;
				else if (sender is MitigatedDateTimePicker)
					(sender as MitigatedDateTimePicker).IsReadOnly = true;
				else if (sender is MitigatedTextBox)
					(sender as MitigatedTextBox).IsReadOnly = true;
		}
	}
}