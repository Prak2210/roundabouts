public class RoundaboutBounds  {

	private RoundaboutBounds leftBound;
	private RoundaboutBounds rightBound;
	private RoundaboutBounds oppositeBound;
	private static Bypass byPass;
	private static double noOfConflictingPedestrians = 0;
	private static double timeInterval = 0.25;
	
	private static double peakHourFactor;
	// P[T] = proportion of demand volume that consists of heavy vehicles
	private double proportionHeavyVehicles;
	// E[T] = passenger car equivalent for heavy vehicles.
	private static double passangerCarEquivalent = 2;
	// f[HV] = heavy-vehicle adjustment factor,
	private double heavyVehicleAdjustmentFactor;

	// Vi =demand volume for movement i (veh/h), and
	private double volumeLeftTurn; // e.g. Vnbl
	private double volumeThrough; // e.g. Vnbt
	private double volumeRightTurn; // e.g. Vnbr
	private double volumeUTurn; // e.g. Vnbu
	
	//Vairables added for multi-lanes
	private double numOfEntryLanes, numOfCirculatingLanes;
	private double capacityOfRightEntryLane;
	private double capacityOfLeftEntryLane;
	private double leftLanePercentile,RightLanePercentile;
	private double bias=0.53;

	// vi = demand flow rate for movement i (veh/h)
	private double volumeFlowRateLeft;
	private double volumeFlowRateRight;
	private double volumeFlowRateRightDuplicate; //Main use is in step 4 and 7
	private double volumeFlowRateUTurn;
	private double volumeFlowRateThrough;
	private double LeftEntryFlowRateperHour, RightEntryFlowRateperHour;

	// v[i,pce] = demand flow rate for movement i (pc/h)
	private double demandFlowRateForMovementLeft; // e.g. v[nbl,pce]
	private double demandFlowRateForMovementRight; // e.g. v[nbr,pce]
	private double demandFlowRateForMovementUTurn; // e.g. v[nbu,pce]
	private double demandFlowRateForMovementThrough; // e.g. v[nbt,pce]

	private double circulatingFlowRates;
	private double exitingFlowRates;
	private double entryFlowRates;
	private double capcaityEntryLane;
	private double pedestrianImpedence;
	private double capacity;
	private double capacityofLeftLane, capacityofRightLane;
	private double entryFlowRatesPerHour;
	private double volumeToCapcityRatio;
	private double controlDelay;
	private double controlDelayByPass;
	private double leftLaneControlDelay, rightLaneControlDelay;
	private double percentile;
	private double capacityByPassLane;
	private double capacityByPass;
	private double entryFlowRatesByPassPerHour;
	private double volumeToCapcityRatioByPass;
	private double vtocLeftLane, vtocRightLane;
	private char los;
	private char losByPass;
	private double controlDelayAggregate;
	private double aggregateEntryFlowRatesPerHour;
	
	

	// Step 1: Convert Movement Demand Volumes to Flow Rates
	
	public double[] calculateMovementDemandVolumeToFlowRates() {
		
		volumeFlowRateLeft = Math.round((volumeLeftTurn / peakHourFactor));
		volumeFlowRateRight = Math.round(volumeFlowRateRightDuplicate / peakHourFactor);
		volumeFlowRateUTurn = Math.round(volumeUTurn / peakHourFactor);
		volumeFlowRateThrough = Math.round(volumeThrough / peakHourFactor);
		return new double[] {volumeFlowRateLeft,volumeFlowRateRight,volumeFlowRateUTurn,volumeFlowRateThrough};
	}

	// Step 2: Adjust Flow Rates for Heavy Vehicles

	public void adjustFlowRatesForHeavyVehicles() {
		
		heavyVehicleAdjustmentFactor = 1 / (1 + proportionHeavyVehicles * (passangerCarEquivalent - 1));
		demandFlowRateForMovementLeft = Math.round(volumeFlowRateLeft / heavyVehicleAdjustmentFactor);
		demandFlowRateForMovementRight = Math.round(volumeFlowRateRight / heavyVehicleAdjustmentFactor);
		demandFlowRateForMovementUTurn = Math.round(volumeFlowRateUTurn / heavyVehicleAdjustmentFactor);
		demandFlowRateForMovementThrough = Math.round(volumeFlowRateThrough / heavyVehicleAdjustmentFactor);
		
	}

	// Step 3: Determine Circulating and Exiting Flow Rates
	
	public void circulatingAndExitingFlowRates() {
		// e.g. 𝑣𝑐,𝑁𝐵,𝑝𝑐𝑒 = 𝑣𝑊𝐵𝑈,𝑝𝑐𝑒 + 𝑣𝑆𝐵𝐿,𝑝𝑐𝑒 +
		// 𝑣𝑆𝐵𝑈,𝑝𝑐𝑒 + 𝑣𝐸𝐵𝑇,𝑝𝑐𝑒 + 𝑣𝐸𝐵𝐿,𝑝𝑐𝑒 + 𝑣𝐸𝐵𝑈,𝑝𝑐𝑒
		double rbu = rightBound.getDemandFlowRateForMovementUTurn();
		double obl = oppositeBound.getDemandFlowRateForMovementLeft();
		double obu = oppositeBound.getDemandFlowRateForMovementUTurn();
		double lbt = leftBound.getDemandFlowRateForMovementThrough();
		double lbl = leftBound.getDemandFlowRateForMovementLeft();
		double lbu = leftBound.getDemandFlowRateForMovementUTurn();

		circulatingFlowRates = (rbu + obl + obu + lbt + lbl + lbu);
		
		double rbr = rightBound.getDemandFlowRateForMovementRight();
		
		if (rightBound.getByPass() == Bypass.YIELD) {
			exitingFlowRates = (obu + lbl + this.demandFlowRateForMovementThrough + rbr
					- rightBound.getDemandFlowRateForMovementRight());
		
		}
	}

	// Step 4: Determine Entry Flow Rates by Lane
	
	public void entryFlowRatesByLanes() {
		
		if(this.numOfEntryLanes==1 && this.numOfCirculatingLanes==1)
			entryFlowRates = (this.demandFlowRateForMovementUTurn + this.demandFlowRateForMovementLeft
				+ this.demandFlowRateForMovementThrough+ Math.round(Math.round(volumeRightTurn/peakHourFactor) / heavyVehicleAdjustmentFactor));
		else //for multilanes
			entryFlowRates = (this.demandFlowRateForMovementUTurn + this.demandFlowRateForMovementLeft
					+ this.demandFlowRateForMovementThrough+ this.demandFlowRateForMovementRight);
	}

	// Step 5 : Determine the Capacity of Each Entry Lane and Bypass Lane as
	public void capacityOfEachEntryLane() 
	{
		//check for single lane or multilane
		if(this.numOfEntryLanes==1 && this.numOfCirculatingLanes==1)
			capcaityEntryLane = Math.round(1380 * Math.exp(-1.02 * Math.pow(10, -3) * circulatingFlowRates));
		if(this.numOfEntryLanes==1 && this.numOfCirculatingLanes==2)
			capcaityEntryLane = Math.round(1420 * Math.exp(-0.85 * Math.pow(10, -3) * circulatingFlowRates));
		if(this.numOfEntryLanes==2 && this.numOfCirculatingLanes==2)
		{
			capcaityEntryLane = Math.round(1420 * Math.exp(-0.85 * Math.pow(10, -3) * circulatingFlowRates)) + Math.round(1350 * Math.exp(-0.92 * Math.pow(10, -3) * circulatingFlowRates));
			capcaityEntryLane/=2;
			setcapacityOfLeftEntryLane(Math.round(1350 * Math.exp(-0.92 * Math.pow(10, -3) * circulatingFlowRates)));
			setcapacityOfRightEntryLane(Math.round(1420 * Math.exp(-0.85 * Math.pow(10, -3) * circulatingFlowRates)));
		}
		if(this.numOfEntryLanes==2 && this.numOfCirculatingLanes==1)
			{
				capcaityEntryLane = Math.round(1420 * Math.exp(-0.91 * Math.pow(10, -3) * circulatingFlowRates));
				setcapacityOfLeftEntryLane(capcaityEntryLane);
				setcapacityOfRightEntryLane(capcaityEntryLane);
			}
		
		if (!(byPass == Bypass.NONE)) {
			capacityOfYieldByPass();
		}
	}

	private void capacityOfYieldByPass() {
			capacityByPassLane = Math.round(1420 * Math.exp(-0.85 * Math.pow(10, -3) * leftBound.getExitingFlowRates()));	
	}

	// Step 6: Determine Pedestrian Impedance to Vehicles
	public void determinePedestrianImpedence() {
			if (entryFlowRates > 881) {
				pedestrianImpedence = 1;
			} else {
				// TODO: Add the other formula nped < 101
				pedestrianImpedence = 1 - (0.000137 * noOfConflictingPedestrians);
			}
		}

	// Step 7: Convert Lane Flow Rates and Capacities into Vehicles per Hour
	public void convertLaneFlowRates() {
			capacity = Math.round(capcaityEntryLane * heavyVehicleAdjustmentFactor * pedestrianImpedence);
			entryFlowRatesPerHour = Math.round(entryFlowRates * heavyVehicleAdjustmentFactor);
			if(this.numOfEntryLanes!=1)
			{
				capacityofLeftLane = Math.round(this.capacityOfLeftEntryLane * heavyVehicleAdjustmentFactor * pedestrianImpedence);
				capacityofRightLane = Math.round(this.capacityOfRightEntryLane * heavyVehicleAdjustmentFactor * pedestrianImpedence);
				LeftEntryFlowRateperHour = entryFlowRatesPerHour*bias;
				RightEntryFlowRateperHour = entryFlowRatesPerHour*(1-bias);	
			}
			if (!(byPass == Bypass.NONE)) 
			{
				capacityByPass = Math.round(capacityByPassLane * heavyVehicleAdjustmentFactor * pedestrianImpedence);
				entryFlowRatesByPassPerHour=demandFlowRateForMovementRight*heavyVehicleAdjustmentFactor;
				
			}
		}

	// Step 8: Compute the Volume-to-Capacity Ratio for Each Lane
	public void computeVolumeToCapacityRatio() {
			
		volumeToCapcityRatio = entryFlowRatesPerHour / capacity;
		if(this.numOfEntryLanes!=1)
		{
			vtocLeftLane = LeftEntryFlowRateperHour/capacityofLeftLane;
			vtocRightLane = RightEntryFlowRateperHour/capacityofRightLane;
		}
		if (byPass == Bypass.YIELD) {
			volumeToCapcityRatioByPass = entryFlowRatesByPassPerHour / capacityByPass;
			volumeToCapcityRatioByPass *= 100;
			volumeToCapcityRatioByPass = Math.round(volumeToCapcityRatioByPass) / 100d;
		}
	}

	// Step 9: Compute the Average Control Delay for Each Lane
	public void computeAverageControlForDelay() {
		double sqrtPart = Math.pow((volumeToCapcityRatio - 1), 2)
				+ (((3600 / capacity) * volumeToCapcityRatio) / (450 * timeInterval));
		
		controlDelay = ((3600 / capacity)
				+ ((900 * timeInterval) * ((volumeToCapcityRatio - 1) + (Math.sqrt(sqrtPart))))
				+ (5 * Math.min(volumeToCapcityRatio, 1)));
		if(this.numOfEntryLanes!=1)
		{
			sqrtPart = Math.pow((vtocLeftLane - 1), 2)
					+ (((3600 / capacityofLeftLane) * vtocLeftLane) / (450 * timeInterval));
			
			leftLaneControlDelay = ((3600 / capacity)
					+ ((900 * timeInterval) * ((vtocLeftLane - 1) + (Math.sqrt(sqrtPart))))
					+ (5 * Math.min(vtocLeftLane, 1)));
			
			sqrtPart = Math.pow((vtocRightLane - 1), 2)
					+ (((3600 / capacityofRightLane) * vtocRightLane) / (450 * timeInterval));
			
			rightLaneControlDelay = ((3600 / capacity)
					+ ((900 * timeInterval) * ((vtocRightLane - 1) + (Math.sqrt(sqrtPart))))
					+ (5 * Math.min(vtocRightLane, 1)));
			
			controlDelay=(leftLaneControlDelay*LeftEntryFlowRateperHour+rightLaneControlDelay*RightEntryFlowRateperHour)/(RightEntryFlowRateperHour+LeftEntryFlowRateperHour);
		}
		
		
		if (byPass == Bypass.YIELD) {
			double sqrtPartByPass = Math.pow((volumeToCapcityRatioByPass - 1), 2)
					+ (((3600 / capacityByPass) * volumeToCapcityRatioByPass) / (450 * timeInterval));
			controlDelayByPass = ((3600 / capacityByPass)
					+ ((900 * timeInterval) * ((volumeToCapcityRatioByPass - 1) + (Math.sqrt(sqrtPartByPass))))
					+ (5 * Math.min(volumeToCapcityRatioByPass, 1)));
			
		}
	}

	
	public void determineLOS() {
		los = calculateLOS(controlDelay, volumeToCapcityRatioByPass);
		if (byPass == Bypass.YIELD) {
			losByPass = calculateLOS(controlDelayByPass, volumeToCapcityRatioByPass);
		}
	}

	public static char calculateLOS(double controlDelay, double volumeToCapacityRatio) {
		char retVal = 0;
		if (volumeToCapacityRatio > 1) {
			retVal = 'F';
		} else if (controlDelay <= 10) {
			retVal = 'A';
		} else if (controlDelay <= 15) {
			retVal = 'B';
		} else if (controlDelay <= 25) {
			retVal = 'C';
		} else if (controlDelay <= 35) {
			retVal = 'D';
		} else if (controlDelay <= 50) {
			retVal = 'E';
		} else {
			retVal = 'F';
		}

		return retVal;

	}

	
	public void computeAverageControlDelay() {
		aggregateEntryFlowRatesPerHour = entryFlowRatesPerHour + entryFlowRatesByPassPerHour;
		
		if (!byPass.equals(Bypass.NONE)) {
			controlDelayAggregate = ((entryFlowRatesPerHour * controlDelay)
					+ (entryFlowRatesByPassPerHour * controlDelayByPass)) / (aggregateEntryFlowRatesPerHour);
			//System.out.println("---"+controlDelayAggregate+" "+entryFlowRatesPerHour+" "+controlDelay+" "+entryFlowRatesByPassPerHour+" "+controlDelayByPass+" ");
		} else {
			controlDelayAggregate = controlDelay;
		}
	}

	// Step 12: Compute 95th Percentile Queues for Each Lane
	
	public double compute95thPercentile() {
		double sqrtPart = Math.pow((volumeToCapcityRatio - 1), 2)
				+ ((3600 * volumeToCapcityRatio/this.capacity)) / (150 * timeInterval);
		percentile = ((900 * timeInterval) * ((volumeToCapcityRatio - 1) + (Math.sqrt(sqrtPart))) * (capacity / 3600));
		
		if(this.numOfEntryLanes!=1)
		{
			sqrtPart = Math.pow((vtocLeftLane - 1), 2)
					+ ((3600 * vtocLeftLane/this.capacityofLeftLane)) / (150 * timeInterval);
			leftLanePercentile = ((900 * timeInterval) * ((vtocLeftLane - 1) + (Math.sqrt(sqrtPart))) * (capacityofLeftLane / 3600));
			
			sqrtPart = Math.pow((vtocRightLane - 1), 2)
					+ ((3600 * vtocRightLane/this.capacityofRightLane)) / (150 * timeInterval);
			RightLanePercentile= ((900 * timeInterval) * ((vtocRightLane - 1) + (Math.sqrt(sqrtPart))) * (capacityofRightLane / 3600));
			
			percentile=RightLanePercentile+leftLanePercentile;
			
		}
		percentile=Math.rint(percentile); //Rounded percentile to nearest integer according to the book
		return percentile;
	}

	public double getDemandFlowRateForMovementLeft() {
		return demandFlowRateForMovementLeft;
	}

	public void setDemandFlowRateForMovementLeft(double demandFlowRateForMovementLeft) {
		this.demandFlowRateForMovementLeft = demandFlowRateForMovementLeft;
	}

	public double getDemandFlowRateForMovementRight() {
		return demandFlowRateForMovementRight;
	}

	public void setDemandFlowRateForMovementRight(double demandFlowRateForMovementRight) {
		this.demandFlowRateForMovementRight = demandFlowRateForMovementRight;
	}

	public double getDemandFlowRateForMovementUTurn() {
		return demandFlowRateForMovementUTurn;
	}

	public void setDemandFlowRateForMovementUTurn(double demandFlowRateForMovementUTurn) {
		this.demandFlowRateForMovementUTurn = demandFlowRateForMovementUTurn;
	}

	public double getDemandFlowRateForMovementThrough() {
		return demandFlowRateForMovementThrough;
	}

	public void setDemandFlowRateForMovementThrough(double demandFlowRateForMovementThrough) {
		this.demandFlowRateForMovementThrough = demandFlowRateForMovementThrough;
	}

	public Bypass getByPass() {
		return byPass;
	}

	public void setByPass(Bypass byPass) {
		RoundaboutBounds.byPass = byPass;
	}

	public RoundaboutBounds getLeftBound() {
		return leftBound;
	}

	public void setLeftBound(RoundaboutBounds leftBound) {
		this.leftBound = leftBound;
	}

	public RoundaboutBounds getRightBound() {
		return rightBound;
	}

	public void setRightBound(RoundaboutBounds rightBound) {
		this.rightBound = rightBound;
	}

	public RoundaboutBounds getOppositeBound() {
		return oppositeBound;
	}

	public void setOppositeBound(RoundaboutBounds oppositeBound) {
		this.oppositeBound = oppositeBound;
	}

	public double getVolumeFlowRateLeft() {
		return volumeFlowRateLeft;
	}

	public void setVolumeFlowRateLeft(double volumeFlowRateLeft) {
		this.volumeFlowRateLeft = volumeFlowRateLeft;
	}

	public double getVolumeThrough() {
		return volumeThrough;
	}

	public void setVolumeThrough(double volumeThrough) {
		this.volumeThrough = volumeThrough;
	}

	public double getVolumeRightTurn() {
		return volumeRightTurn;
	}

	public void setVolumeRightTurn(double volumeRightTurn) {
		this.volumeRightTurn = volumeRightTurn;
	}

	public double getVolumeLeftTurn() {
		return volumeLeftTurn;
	}

	public void setVolumeLeftTurn(double volumeLeftTurn) {
		this.volumeLeftTurn = volumeLeftTurn;
	}

	public double getVolumeUTurn() {
		return volumeUTurn;
	}

	public void setVolumeUTurn(double volumeUTurn) {
		this.volumeUTurn = volumeUTurn;
	}

	public double getControlDelay() {
		return controlDelay;
	}

	public void setControlDelay(double controlDelay) {
		this.controlDelay = controlDelay;
	}

	public double getExitingFlowRates() {
		return exitingFlowRates;
	}

	public double getVolumeFlowRateRight() {
		return volumeFlowRateRight;
	}

	@Override
	public String toString() {
		return "";
	}

	public double getVolumeFlowRateUTurn() {
		return volumeFlowRateUTurn;
	}

	public double getVolumeFlowRateThrough() {
		return volumeFlowRateThrough;
	}

	public double getCirculatingFlowRates() {
		return circulatingFlowRates;
	}

	public double getEntryFlowRates() {
		return entryFlowRates;
	}

	public double getCapcaityEntryLane() {
		return capcaityEntryLane;
	}

	public double getPedestrianImpedence() {
		return pedestrianImpedence;
	}

	public double getCapacity() {
		return capacity;
	}

	public double getEntryFlowRatesPerHour() {
		return entryFlowRatesPerHour;
	}

	public double getPercentile() {
		return percentile;
	}

	public double getVolumeToCapcityRatio() {
		return volumeToCapcityRatio;
	}

	public double getCapacityByPassLane() {
		return capacityByPassLane;
	}

	public double getCapacityByPass() {
		return capacityByPass;
	}
	public double getHeavyVehicleAdjustmentFactor() {
		return heavyVehicleAdjustmentFactor;
	}
	public double getEntryFlowRatesByPassPerHour() {
		return entryFlowRatesByPassPerHour;
	}

	public double getVolumeToCapcityRatioByPass() {
		return volumeToCapcityRatioByPass;
	}

	public double getControlDelayByPass() {
		return controlDelayByPass;
	}

	public char getLos() {
		return los;
	}

	public char getLosByPass() {
		return losByPass;
	}

	public double getControlDelayAggregate() {
		return controlDelayAggregate;
	}

	public double getAggregateEntryFlowRatesPerHour() {
		return aggregateEntryFlowRatesPerHour;
	}

	public static double getNoOfConflictingPedestrians() {
		return noOfConflictingPedestrians;
	}

	public static void setNoOfConflictingPedestrians(double noOfConflictingPedestrians) {
		RoundaboutBounds.noOfConflictingPedestrians = noOfConflictingPedestrians;
	}

	public static double getTimeInterval() {
		return timeInterval;
	}

	public static void setTimeInterval(double timeInterval) {
		RoundaboutBounds.timeInterval = timeInterval;
	}

	public static double getPeakHourFactor() {
		return peakHourFactor;
	}

	public static void setPeakHourFactor(double peakHourFactor) {
		RoundaboutBounds.peakHourFactor = peakHourFactor;
	}

	public static double getPassangerCarEquivalent() {
		return passangerCarEquivalent;
	}

	public static void setPassangerCarEquivalent(double passangerCarEquivalent) {
		RoundaboutBounds.passangerCarEquivalent = passangerCarEquivalent;
	}

	public double getVolumeFlowRateRightDuplicate() {
		return volumeFlowRateRightDuplicate;
	}

	public void setVolumeFlowRateRightDuplicate(double volumeFlowRateRightDuplicate) {
		this.volumeFlowRateRightDuplicate = volumeFlowRateRightDuplicate;
	}

	public double getNumOfEntryLanes() {
		return numOfEntryLanes;
	}

	public void setNumOfEntryLanes(double numOfEntryLanes) {
		this.numOfEntryLanes = numOfEntryLanes;
	}

	public double getnumOfCirculatingLanes() {
		return numOfCirculatingLanes;
	}

	public void setnumOfCirculatingLanes(double numOfCirculatingLanes) {
		this.numOfCirculatingLanes = numOfCirculatingLanes;
	}

	public double getProportionHeavyVehicles() {
		return proportionHeavyVehicles;
	}

	public void setProportionHeavyVehicles(double proportionHeavyVehicles) {
		this.proportionHeavyVehicles = proportionHeavyVehicles;
	}

	public double getcapacityOfRightEntryLane() {
		return capacityOfRightEntryLane;
	}

	public void setcapacityOfRightEntryLane(double capacityOfrightLane) {
		this.capacityOfRightEntryLane = capacityOfrightLane;
	}

	public double getcapacityOfLeftEntryLane() {
		return capacityOfLeftEntryLane;
	}

	public void setcapacityOfLeftEntryLane(double capacityOfleftLane) {
		this.capacityOfLeftEntryLane = capacityOfleftLane;
	}

	public double getCapacityofLeftLane() {
		return capacityofLeftLane;
	}

	public void setCapacityofLeftLane(double capacityofLeftLane) {
		this.capacityofLeftLane = capacityofLeftLane;
	}

	public double getCapacityofRightLane() {
		return capacityofRightLane;
	}

	public void setCapacityofRightLane(double capacityofRightLane) {
		this.capacityofRightLane = capacityofRightLane;
	}

	public double getVtocLeftLane() {
		return vtocLeftLane;
	}

	public double getVtocRightLane() {
		return vtocRightLane;
	}
	

}
