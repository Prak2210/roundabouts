import java.util.ArrayList;


public class RoundaboutComputeEngine {
	private RoundaboutBounds northBound;
	private RoundaboutBounds southBound;
	private RoundaboutBounds westBound;
	private RoundaboutBounds eastBound;
	Boolean debug=true;
	private double calculateAggregateIntersection=0;
	public double getCalculateAggregateIntersection() {
		return calculateAggregateIntersection;
	}

	public void setCalculateAggregateIntersection(double calculateAggregateIntersection) {
		this.calculateAggregateIntersection = calculateAggregateIntersection;
	}

	
	public RoundaboutComputeEngine(Double[] volumes) {
		
		Bypass[] bypassConfigurations={Bypass.NONE,Bypass.YIELD};
		
		northBound = new RoundaboutNorthBound();
		southBound = new RoundaboutSouthBound();
		westBound = new RoundaboutWestBound();
		eastBound = new RoundaboutEastBound();
		
		
		if(volumes[24]==0)
			eastBound.setByPass(bypassConfigurations[0]);
		else
			eastBound.setByPass(bypassConfigurations[1]);
		
		if(volumes[25]==0)
			westBound.setByPass(bypassConfigurations[0]);
		else
			westBound.setByPass(bypassConfigurations[1]);
		
		if(volumes[26]==0)
			northBound.setByPass(bypassConfigurations[0]);
		else
			northBound.setByPass(bypassConfigurations[1]);

		if(volumes[27]==0)
			southBound.setByPass(bypassConfigurations[0]);
		else
			southBound.setByPass(bypassConfigurations[1]);
		
		RoundaboutBounds.setNoOfConflictingPedestrians(0);
		RoundaboutBounds.setPassangerCarEquivalent(volumes[22]);
		RoundaboutBounds.setPeakHourFactor(volumes[17]);
		
		for(int i=0;i<=volumes.length;i++)
		{			
			
			eastBound.setVolumeUTurn(volumes[1]);
			eastBound.setVolumeLeftTurn(volumes[2]);
			eastBound.setVolumeThrough(volumes[3]);
			eastBound.setVolumeRightTurn(volumes[4]);
			eastBound.setVolumeFlowRateRightDuplicate(volumes[4]);
			eastBound.setNumOfEntryLanes(volumes[28]);
			eastBound.setnumOfCirculatingLanes(volumes[32]);
			eastBound.setProportionHeavyVehicles(volumes[18]);
			eastBound.setLeftBound(southBound);
			eastBound.setRightBound(northBound);
			eastBound.setOppositeBound(westBound);
			
			westBound.setVolumeUTurn(volumes[5]);
			westBound.setVolumeLeftTurn(volumes[6]);
			westBound.setVolumeThrough(volumes[7]);
			westBound.setVolumeRightTurn(0);
			westBound.setVolumeFlowRateRightDuplicate(volumes[8]);
			westBound.setNumOfEntryLanes(volumes[29]);
			westBound.setnumOfCirculatingLanes(volumes[33]);
			westBound.setProportionHeavyVehicles(volumes[19]);
			westBound.setLeftBound(northBound);
			westBound.setRightBound(southBound);
			westBound.setOppositeBound(eastBound);
			
			northBound.setVolumeUTurn(volumes[13]);
			northBound.setVolumeLeftTurn(volumes[14]);
			northBound.setVolumeThrough(volumes[15]);
			northBound.setVolumeRightTurn(volumes[16]);
			northBound.setVolumeFlowRateRightDuplicate(volumes[16]);
			northBound.setnumOfCirculatingLanes(volumes[34]);
			northBound.setNumOfEntryLanes(volumes[30]);
			northBound.setProportionHeavyVehicles(volumes[20]);
			northBound.setLeftBound(eastBound);
			northBound.setRightBound(westBound);
			northBound.setOppositeBound(southBound);
			
			southBound.setVolumeUTurn(volumes[9]);
			southBound.setVolumeLeftTurn(volumes[10]);
			southBound.setVolumeThrough(volumes[11]);
			southBound.setVolumeRightTurn(0);
			southBound.setVolumeFlowRateRightDuplicate(volumes[12]);
			southBound.setNumOfEntryLanes(volumes[31]);
			southBound.setnumOfCirculatingLanes(volumes[35]);
			southBound.setProportionHeavyVehicles(volumes[21]);
			southBound.setLeftBound(westBound);
			southBound.setRightBound(eastBound);
			southBound.setOppositeBound(northBound);
	
		}

	}

	public ArrayList<RoundaboutBounds> computeSteps() {
		
		ArrayList<RoundaboutBounds> output=new ArrayList<>();
		RoundaboutBounds nb=northBound;
		RoundaboutBounds eb=eastBound;
		RoundaboutBounds wb=westBound;
		RoundaboutBounds sb=southBound;
        
		nb.calculateMovementDemandVolumeToFlowRates();
		sb.calculateMovementDemandVolumeToFlowRates();
		eb.calculateMovementDemandVolumeToFlowRates();
		wb.calculateMovementDemandVolumeToFlowRates();
		
		nb.adjustFlowRatesForHeavyVehicles();
		sb.adjustFlowRatesForHeavyVehicles();
		eb.adjustFlowRatesForHeavyVehicles();
		wb.adjustFlowRatesForHeavyVehicles();

		nb.circulatingAndExitingFlowRates();
		sb.circulatingAndExitingFlowRates();
		eb.circulatingAndExitingFlowRates();
		wb.circulatingAndExitingFlowRates();

		if (debug==true) {

			System.out.println("====== STEP 3 ===== ");
			System.out.println("v[c,NB,pce] :" + northBound.getCirculatingFlowRates());
			System.out.println("v[c,EB,pce] :" + eastBound.getCirculatingFlowRates());
			System.out.println("v[c,SB,pce] :" + southBound.getCirculatingFlowRates());
			System.out.println("v[c,WB,pce] :" + westBound.getCirculatingFlowRates());
			System.out.println("v[c,WB,pce] :" + northBound.getExitingFlowRates());
			System.out.println("====== END OF STEP 3 ====== ");
		}
		nb.entryFlowRatesByLanes();
		sb.entryFlowRatesByLanes();
		eb.entryFlowRatesByLanes();
		wb.entryFlowRatesByLanes();

		if (debug==true) {

			System.out.println("====== STEP 4 ===== ");
			System.out.println("v[e,NB,pce] :" + northBound.getEntryFlowRates());
			System.out.println("v[e,EB,pce] :" + eastBound.getEntryFlowRates());
			System.out.println("v[e,SB,pce] :" + southBound.getEntryFlowRates());
			System.out.println("v[e,WB,pce] :" + westBound.getEntryFlowRates());
			System.out.println("====== END OF STEP 4 ====== ");
		}

		nb.capacityOfEachEntryLane();
		sb.capacityOfEachEntryLane();
		eb.capacityOfEachEntryLane();
		wb.capacityOfEachEntryLane();

		if (debug==true) {
			System.out.println("===== STEP 5 =====");
			System.out.println("c[NB,pce] :" + northBound.getCapcaityEntryLane());
			System.out.println("c[pce,EB] :" + eastBound.getCapcaityEntryLane());
			System.out.println("c[pce,SB] :" + southBound.getCapcaityEntryLane());
			System.out.println("c[pce,WB] :" + westBound.getCapcaityEntryLane());
			System.out.println("====== END OF STEP 5 ====== ");
		}

		nb.determinePedestrianImpedence();
		sb.determinePedestrianImpedence();
		eb.determinePedestrianImpedence();
		wb.determinePedestrianImpedence();

		if (debug==true) {
			System.out.println("===== STEP 6 =====");
			System.out.println("NB f[ped]:" + northBound.getPedestrianImpedence());
			System.out.println("EB f[ped]:" + eastBound.getPedestrianImpedence());
			System.out.println("SB f[ped]:" + southBound.getPedestrianImpedence());
			System.out.println("WB f[ped]:" + westBound.getPedestrianImpedence());
			System.out.println("====== END OF STEP 6====== ");
		}

		nb.convertLaneFlowRates();
		sb.convertLaneFlowRates();
		eb.convertLaneFlowRates();
		wb.convertLaneFlowRates();

		if (debug==true) {
			System.out.println("===== STEP 7 =====");
			System.out.println("NB x:" + northBound.getCapacity());
			System.out.println("EB x:" + eastBound.getCapacity());
			System.out.println("SB x:" + southBound.getCapacity());
			System.out.println("WB x:" + westBound.getCapacity());
			System.out.println("WB x:" + westBound.getCapacityByPass());

			System.out.println("NB x:" + northBound.getEntryFlowRatesPerHour());
			System.out.println("EB x:" + eastBound.getEntryFlowRatesPerHour());
			System.out.println("SB x:" + southBound.getEntryFlowRatesPerHour());
			System.out.println("WB x:" + westBound.getEntryFlowRatesPerHour());
			System.out.println("WB x:" + westBound.getEntryFlowRatesByPassPerHour());
			System.out.println("====== END OF STEP 7====== ");
		}

		nb.computeVolumeToCapacityRatio();
		sb.computeVolumeToCapacityRatio();
		eb.computeVolumeToCapacityRatio();
		wb.computeVolumeToCapacityRatio();

		if (debug==true) {

			System.out.println("===== STEP 8 =====");
			System.out.println("NB x:" + northBound.getVolumeToCapcityRatio());
			System.out.println("EB x:" + eastBound.getVolumeToCapcityRatio());
			System.out.println("SB x:" + southBound.getVolumeToCapcityRatio());
			System.out.println("WB x:" + westBound.getVolumeToCapcityRatio());
			System.out.println("WB x:" + westBound.getVolumeToCapcityRatioByPass());
			System.out.println("====== END OF STEP 8====== ");
		}

		nb.computeAverageControlForDelay();
		sb.computeAverageControlForDelay();
		eb.computeAverageControlForDelay();
		wb.computeAverageControlForDelay();

		if (debug==true) {

			System.out.println("===== STEP 9 =====");
			System.out.println("NB x:" + northBound.getControlDelay());
			System.out.println("EB x:" + eastBound.getControlDelay());
			System.out.println("SB x:" + southBound.getControlDelay());
			System.out.println("WB x:" + westBound.getControlDelay());
			System.out.println("WB x:" + westBound.getControlDelayByPass());
			System.out.println("====== END OF STEP 9====== ");
		}

		nb.determineLOS();
		sb.determineLOS();
		eb.determineLOS();
		wb.determineLOS();

		nb.computeAverageControlDelay();
		sb.computeAverageControlDelay();
		eb.computeAverageControlDelay();
		wb.computeAverageControlDelay();

		calculateAggregateIntersection = calculateAggregateIntersection();
		
		if (debug==true)
			System.out.println("Step 11 ========= " + calculateAggregateIntersection + "  "
					+ RoundaboutBounds.calculateLOS(calculateAggregateIntersection, -1));


		nb.compute95thPercentile();
		sb.compute95thPercentile();
		eb.compute95thPercentile();
		wb.compute95thPercentile();
		if(debug==true)
		{
			System.out.println("===== STEP 12 =====");
			System.out.println("NB x:" + northBound.getPercentile());
			System.out.println("EB x:" + eastBound.getPercentile());
			System.out.println("SB x:" + southBound.getPercentile());
			System.out.println("WB x:" + westBound.getPercentile());
		}

		
		output.add(eb);
		output.add(wb);
		output.add(nb);
		output.add(sb);
		
		return output;
	}

	double calculateAggregateIntersection() 
	{
		double northBoundControlDelay = northBound.getControlDelayAggregate();
		double northBoundEntryFlowRatesPerHour = northBound.getAggregateEntryFlowRatesPerHour();
		double southBoundControlDelay = southBound.getControlDelayAggregate();
		double eastBoundControlDelay = eastBound.getControlDelayAggregate();
		double westBoundControlDelay = westBound.getControlDelayAggregate();
		double southBoundEntryFlowRatesPerHour = southBound.getAggregateEntryFlowRatesPerHour();
		double eastBoundEntryFlowRatesPerHour = eastBound.getAggregateEntryFlowRatesPerHour();
		double westBoundEntryFlowRatesPerHour = westBound.getAggregateEntryFlowRatesPerHour();
		double intersectionControlDelay = ((northBoundControlDelay * northBoundEntryFlowRatesPerHour)
				+ (southBoundControlDelay * southBoundEntryFlowRatesPerHour)
				+ (eastBoundControlDelay * eastBoundEntryFlowRatesPerHour)
				+ (westBoundControlDelay * westBoundEntryFlowRatesPerHour))
				/ (northBoundEntryFlowRatesPerHour + southBoundEntryFlowRatesPerHour + eastBoundEntryFlowRatesPerHour
						+ westBoundEntryFlowRatesPerHour);
		return intersectionControlDelay;
	}
	

}
