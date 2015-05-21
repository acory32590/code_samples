/// <summary>
/// Interaction logic for CDDView.xaml
/// </summary>
public partial class CDDHeaderView : UserControl
{
	public CDDHeaderView(string claimRefno)
	{
		InitializeComponent();
		DataContext = new CDDViewModel(claimRefno);
	}

	public CDDHeaderView(string claimRefno, ServiceXGMainViewModel viewModel)
	{
		InitializeComponent();
		DataContext = new CDDViewModel(claimRefno, viewModel);
	}

	private void UIElement_OnKeyDown(object sender, KeyEventArgs e)
	{
		if (Keyboard.IsKeyDown(Key.LeftCtrl))
		{
			if (e.Key == Key.OemPlus || e.Key == Key.Add)
				TabItems.SelectedIndex = 1;
			if (e.Key == Key.OemMinus || e.Key == Key.Subtract)
				TabItems.SelectedIndex = 0;
		}
	}
}