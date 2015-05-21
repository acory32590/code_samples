public class CDDViewModel : BasePopUpViewModel
{
	#region Commands

	public ICommand ClearLineDataCommand { get; set; }

	#endregion

	#region Properties

	private readonly IAuxService _auxService;
	private readonly IClaimService _clmService;

	private ClaimProcessingSessionViewModel _parentViewModel;
	public ClaimProcessingSessionViewModel ParentViewModel
	{
		get
		{
			return _parentViewModel;
		}
		set
		{
			_parentViewModel = value;
			NotifyPropertyChanged("ParentViewModel");
		}
	}

	private Auxiliary _auxFile;
	public Auxiliary AuxFile
	{
		get
		{
			return _auxFile;
		}
		set
		{
			_auxFile = value;
			NotifyPropertyChanged("AuxFile");
		}
	}

	private NotifiableObservableCollection<Auxiliary.AuxiliaryData> _detailAux;
	public NotifiableObservableCollection<Auxiliary.AuxiliaryData> DetailAux
	{
		get
		{
			return _detailAux;
		}
		set
		{
			_detailAux = value;
			NotifyPropertyChanged("DetailAux");
		}
	}

	private ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>> _lineAux;
	public ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>> LineAux
	{
		get
		{
			return _lineAux;
		}
		set
		{
			_lineAux = value;
			NotifyPropertyChanged("LineAux");
		}
	}

	private NotifiableObservableCollection<Auxiliary.AuxiliaryData> _selectedLineAux;
	public NotifiableObservableCollection<Auxiliary.AuxiliaryData> SelectedLineAux
	{
		get
		{
			return _selectedLineAux;
		}
		set
		{
			_selectedLineAux = value;
			NotifyPropertyChanged("SelectedLineAux");
		}
	}

	private Dictionary<string, string> _claimLines; 
	public Dictionary<string, string> ClaimLines
	{
		get { return _claimLines; }
		set { _claimLines = value; NotifyPropertyChanged("ClaimLines"); }
	}

	private string _selectedLine;
	public string SelectedLine
	{
		get { return _selectedLine; }
		set
		{
			_selectedLine = value;
			if (!String.IsNullOrEmpty(_selectedLine) && LineAux != null && LineAux.Count >= Convert.ToInt32(_selectedLine))
				SelectedLineAux = LineAux[Convert.ToInt32(_selectedLine)-1];
			if (ParentViewModel != null && !ParentViewModel.ProcessingViewModel.SelectedLine.Equals(value))
				ParentViewModel.ProcessingViewModel.SelectedLine = value;
			NotifyPropertyChanged("SelectedLine");
		}
	}

	public bool IsReadOnly;
	public bool IsNotReadOnly
	{
		get { return !IsReadOnly; }
	}

	#endregion

	#region Constructor

	public CDDViewModel(string claimId)
	{
		Key = claimId;
		IsReadOnly = true;

		_auxService = Container.Resolve<IAuxService>();
		_clmService = Container.Resolve<IClaimService>();

		#region Register Base View Model Events

		GetEventHandler += Get;
		RefreshEventHandler += Refresh;

		#endregion
	}

	public CDDViewModel(string claimId, ServiceXGMainViewModel parentViewModel)
	{
		Key = claimId;
		IsReadOnly = false;

		_auxService = Container.Resolve<IAuxService>();
		_clmService = Container.Resolve<IClaimService>();

		if (parentViewModel is ClaimProcessingSessionViewModel)
			_parentViewModel = parentViewModel as ClaimProcessingSessionViewModel;

		#region Register Base View Model Events

		if (_parentViewModel == null)
		{
			IsReadOnly = true;
			GetEventHandler += Get;
			RefreshEventHandler += Refresh;
		}
		else
		{
			InitializeCollections();
			OKEventHandler += UpdateParentAux;
			ClearLineDataCommand = new DelegateCommand(DeleteLineAuxInfo, IsCDDNotReadOnly);
		}

		Mouse.OverrideCursor = null;

		#endregion
	}

	#endregion

	#region BaseViewModel Implementation

	public void Get(object sender, GetEventArgs args)
	{
		_clmService.GetBasicClaimByClaimID(Key, clmResult =>
		{
			if (clmResult != null && clmResult.IsCompleted && clmResult.Result != null)
			{
				var clmDetail = clmResult.Result;

				ClaimLines = GenerateLineDropdown(clmDetail.LineItems.Count);
				if(ClaimLines.Count > 0)
					SelectedLine = "1";

				_auxService.LoadDispAux("CLM-CUST-DATA", Key, result =>
				{
					if (result != null && result.IsCompleted && result.Result != null)
					{
						AuxFile = result.Result;
						DetailAux = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();
						LineAux =
							new ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>>();
						for (int i = 0; i < ClaimLines.Count; i++)
							LineAux.Add(new NotifiableObservableCollection<Auxiliary.AuxiliaryData>());

						foreach (Auxiliary.AuxiliaryData auxData in AuxFile.Data)
						{
							if (!String.IsNullOrEmpty(auxData.DispFlag))
							{
								if (auxData.ColumnType == "UV")
									DetailAux.Add(auxData);
								else
									// If empty, no lines exist, don't bother loading junk data
									if (LineAux.Count != 0)
									{
										var tempCollection = ToUVCollection(auxData, LineAux, AuxFile);
										for (int i = 0; i < tempCollection.Count; i++)
											LineAux[i].Add(tempCollection[i]);
									}
							}
						}

						// Set up property changed handler for data for isdirty
						DetailAux.ItemPropertyChanged += NotifyDomainPropertyChanged;
						if (!String.IsNullOrEmpty(SelectedLine))
							foreach (var collection in LineAux)
								collection.ItemPropertyChanged += NotifyDomainPropertyChanged;

						if (LineAux.Count > 0)
							SelectedLineAux = LineAux[0];

						//once domain model object is loaded set it to geteventargs.asyncall back otherwise lock warning wouldn't show up
						args.AsyncCallBack(AuxFile);
					}
				}, this);
			}
		}, this);
	}

	public void Refresh(object sender, EventArgs args)
	{
		Refresh();
	}

	public void Refresh()
	{
		_clmService.GetBasicClaimByClaimID(Key, clmResult =>
		{
			if (clmResult != null && clmResult.IsCompleted && clmResult.Result != null)
			{
				var clmDetail = clmResult.Result;

				ClaimLines = GenerateLineDropdown(clmDetail.LineItems.Count);
				if (ClaimLines.Count > 0)
					SelectedLine = "1";

				_auxService.LoadDispAux("CLM-CUST-DATA", Key, result =>
				{
					if (result != null && result.IsCompleted && result.Result != null)
					{
						AuxFile = result.Result;
						DetailAux = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();
						LineAux =
							new ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>>();
						for (int i = 0; i < ClaimLines.Count; i++)
							LineAux.Add(new NotifiableObservableCollection<Auxiliary.AuxiliaryData>());

						foreach (Auxiliary.AuxiliaryData auxData in AuxFile.Data)
						{
							if (!String.IsNullOrEmpty(auxData.DispFlag))
							{
								if (auxData.ColumnType == "UV")
									DetailAux.Add(auxData);
								else
									// If empty, no lines exist, don't bother loading junk data
									if (LineAux.Count != 0)
									{
										var tempCollection = ToUVCollection(auxData, LineAux, AuxFile);
										for (int i = 0; i < tempCollection.Count; i++)
											LineAux[i].Add(tempCollection[i]);
									}
							}
						}

						// Set up property changed handler for data for isdirty
						DetailAux.ItemPropertyChanged += NotifyDomainPropertyChanged;
						if (!String.IsNullOrEmpty(SelectedLine))
							foreach (var collection in LineAux)
								collection.ItemPropertyChanged += NotifyDomainPropertyChanged;

						if (LineAux.Count > 0)
							SelectedLineAux = LineAux[0];
					}
				}, this);
			}
		}, this);
	}

	#endregion

	#region Private Methods

	private void InitializeCollections()
	{
		DetailAux = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();
		foreach (var auxData in ParentViewModel.ProcessingViewModel.DetailAux)
			DetailAux.Add(auxData.Clone() as Auxiliary.AuxiliaryData);

		LineAux = new ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>>();
		foreach (
			NotifiableObservableCollection<Auxiliary.AuxiliaryData> data in
				ParentViewModel.ProcessingViewModel.LineAux)
		{
			var tempCollection = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();
			foreach(var auxData in data)
				tempCollection.Add(auxData.Clone() as Auxiliary.AuxiliaryData);

			LineAux.Add(tempCollection);
		}

		ClaimLines = new Dictionary<string, string>();
		ClaimLines = GenerateLineDropdown(ParentViewModel.ProcessingViewModel.ClaimDetail.LineItems.Count);
		SelectedLine = ParentViewModel.ProcessingViewModel.SelectedLine;
	}

	private void UpdateParentAux(object sender, SaveEventArgs saveEventArgs)
	{
		ParentViewModel.ProcessingViewModel.DetailAux = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();
		foreach (var auxData in DetailAux)
			ParentViewModel.ProcessingViewModel.DetailAux.Add(auxData.Clone() as Auxiliary.AuxiliaryData);

		ParentViewModel.ProcessingViewModel.LineAux = new ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>>();
		foreach (
			NotifiableObservableCollection<Auxiliary.AuxiliaryData> data in LineAux)
		{
			var tempCollection = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();
			foreach (var auxData in data)
				tempCollection.Add(auxData.Clone() as Auxiliary.AuxiliaryData);

			ParentViewModel.ProcessingViewModel.LineAux.Add(tempCollection);
		}

		ParentViewModel.ProcessingViewModel.SelectedLine = SelectedLine;
		ParentViewModel.ProcessingViewModel.GetCustomerDefinedDataForSave();
	}

	private void DeleteLineAuxInfo()
	{
		var numSelectedLine = Convert.ToInt32(SelectedLine) - 1;
		if (!String.IsNullOrEmpty(SelectedLine) && numSelectedLine < LineAux.Count && numSelectedLine >= 0)
		{
			foreach (var item in LineAux[numSelectedLine])
				if (item.DispFlag == "U")
					item.UvValue = null;
		}
	}

	public bool IsCDDReadOnly()
	{
		return (AuthorizationService.GetLevelofAccess(CommonMenuKeys.ClaimCustomerDefinedData) <= Permission.View || IsReadOnly);
	}

	public bool IsCDDNotReadOnly()
	{
		return (AuthorizationService.GetLevelofAccess(CommonMenuKeys.ClaimCustomerDefinedData) > Permission.View && !IsReadOnly);
	}

	#endregion

	#region Public Methods

	public static Dictionary<string, string> GenerateLineDropdown(int lines)
	{
		var claimLines = new Dictionary<string, string>();
		if (lines >= 0)
		{
			for (int i = 1; i < lines + 1; i++)
				claimLines.Add(i.ToString(CultureInfo.InvariantCulture), i.ToString(CultureInfo.InvariantCulture));
		}

		return claimLines;
	}

	public static NotifiableObservableCollection<Auxiliary.AuxiliaryData> ToUVCollection(Auxiliary.AuxiliaryData mvData, 
		ObservableCollection<NotifiableObservableCollection<Auxiliary.AuxiliaryData>> lineAux,
		Auxiliary auxFile)
	{
		NotifiableObservableCollection<Auxiliary.AuxiliaryData> tempList = new NotifiableObservableCollection<Auxiliary.AuxiliaryData>();

		for (int j = 0; j < lineAux.Count; j++)
		{
			var tempData = mvData.Clone() as Auxiliary.AuxiliaryData;
			tempData.ColumnType = "UV";
			tempData.UvName = tempData.Data.Columns[0].ColumnName;
			if (tempData.Data.Rows.Count > j)
				tempData.UvValue = tempData.Data.Rows[j][0];
			else
				tempData.UvValue = null;

			var invalidSourceError = tempData.Data.Rows.Count > j
				? tempData.GetInvalidSourceInfo(tempData.UvName, tempData.Data.Rows[j])
				: null;
			if(invalidSourceError != null)
				tempData.InvalidDataInfoMap.Add(tempData.UvName, invalidSourceError);

			DataRow definitionRow = auxFile.GetDefinitionRowForProperty(tempData.Data.Columns[0].ColumnName);
			if (definitionRow != null)
				tempData.UvDataType = definitionRow["DataType"] as string;

			tempList.Add(tempData);
		}

		return tempList;
	}

	public static Auxiliary.AuxiliaryData ToMVData(NotifiableObservableCollection<Auxiliary.AuxiliaryData> uvDataList)
	{
		if (uvDataList == null || uvDataList.Count == 0)
			return null;

		Auxiliary.AuxiliaryData mvData = uvDataList[0].Clone() as Auxiliary.AuxiliaryData;
		mvData.UvName = "";
		mvData.UvDataType = "";
		mvData.ColumnType = "MV";

		for (int i = 1; i < uvDataList.Count; i++)
		{
			if (mvData.Data.Rows.Count <= i)
				mvData.Data.Rows.Add(mvData.Data.NewRow());
			mvData.Data.Rows[i][0] = uvDataList[i].UvValue;
		}

		return mvData;
	}

	#endregion
}