
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class Controller
{
	static Scanner sc=new Scanner(System.in);

	// All modules implemented
	
	public static void main (String s[]) throws ClassNotFoundException {
		try {
 			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/RBT","root","");
			Statement st=con.createStatement();
			int j=0;
			ResultSet rs=st.executeQuery("select * from RoundAbouts");
			Double volumes[]=new Double[36];
			
			RoundaboutComputeEngine rbc;
			long startTime = System.currentTimeMillis();
			
			System.out.println("Enter index1 & index2 for start: ");
			int firstindex=sc.nextInt();
			int lastindex=sc.nextInt();

			String query="update RoundAbouts set `EB_Left_Bound`=?, `WB_Left_Bound`=?, `NB_Left_Bound`=?, `SB_Left_Bound`=?,"
					+ "`EB_Right_Bound`=?, `WB_Right_Bound`=?, `NB_Right_Bound`=?, `SB_Right_Bound`=?, "
					+ "`EB_Opposite_Bound`=?, `WB_Opposite_Bound`=?, `NB_Opposite_Bound`=?, `SB_Opposite_Bound`=?, "
					+ "`EB_L_Demand_Vol_Flow_Rate_veh/h`=?, `WB_L_Demand_Vol_Flow_Rate_veh/h`=?, `NB_L_Demand_Vol_Flow_Rate_veh/h`=?, `SB_L_Demand_Vol_Flow_Rate_veh/h`=?, "
					+ "`EB_R_Demand_Vol_Flow_Rate_veh/h`=?, `WB_R_Demand_Vol_Flow_Rate_veh/h`=?, `NB_R_Demand_Vol_Flow_Rate_veh/h`=?, `SB_R_Demand_Vol_Flow_Rate_veh/h`=?, "
					+ "`EB_U_Demand_Vol_Flow_Rate_veh/h`=?, `WB_U_Demand_Vol_Flow_Rate_veh/h`=?, `NB_U_Demand_Vol_Flow_Rate_veh/h`=?, `SB_U_Demand_Vol_Flow_Rate_veh/h`=?, "
					+ "`EB_T_Demand_Vol_Flow_Rate_veh/h`=?, `WB_T_Demand_Vol_Flow_Rate_veh/h`=?, `NB_T_Demand_Vol_Flow_Rate_veh/h`=?, `SB_T_Demand_Vol_Flow_Rate_veh/h`=?, "
					+ "`EB_Heavy_Veh_AF`=?, `WB_Heavy_Veh_AF`=?, `NB_Heavy_Veh_AF`=?, `SB_Heavy_Veh_AF`=?, "
					+ "`EB_L_Demand_Flow_Rate_pc/h`=?, `WB_L_Demand_Flow_Rate_pc/h`=?, `NB_L_Demand_Flow_Rate_pc/h`=?, `SB_L_Demand_Flow_Rate_pc/h`=?, "
					+ "`EB_R_Demand_Flow_Rate_pc/h`=?, `WB_R_Demand_Flow_Rate_pc/h`=?, `NB_R_Demand_Flow_Rate_pc/h`=?, `SB_R_Demand_Flow_Rate_pc/h`=?, "
					+ "`EB_U_Demand_Flow_Rate_pc/h`=?, `WB_U_Demand_Flow_Rate_pc/h`=?, `NB_U_Demand_Flow_Rate_pc/h`=?, `SB_U_Demand_Flow_Rate_pc/h`=?, "
					+ "`EB_T_Demand_Flow_Rate_pc/h`=?, `WB_T_Demand_Flow_Rate_pc/h`=?, `NB_T_Demand_Flow_Rate_pc/h`=?, `SB_T_Demand_Flow_Rate_pc/h`=?, "
					+ "`EB_Circulating_Flow_Rate`=?, `WB_Circulating_Flow_Rate`=?, `NB_Circulating_Flow_Rate`=?, `SB_Circulating_Flow_Rate`=?, "
					+ "`EB_Exiting_Flow_Rate`=?, `WB_Exiting_Flow_Rate`=?, `NB_Exiting_Flow_Rate`=?, `SB_Exiting_Flow_Rate`=?, "
					+ "`EB_Entry_Flow_Rate`=?, `WB_Entry_Flow_Rate`=?, `NB_Entry_Flow_Rate`=?, `SB_Entry_Flow_Rate`=?, "
					+ "`EB_Entry_Lane_Capcaity`=?, `WB_Entry_Lane_Capcaity`=?, `NB_Entry_Lane_Capcaity`=?, `SB_Entry_Lane_Capcaity`=?, "
					+ "`EB_Bypass_Capacity`=?, `WB_Bypass_Capacity`=?, `NB_Bypass_Capacity`=?, `SB_Bypass_Capacity`=?, "
					+ "`EB_Pedestrian_Impedence`=?, `WB_Pedestrian_Impedence`=?, `NB_Pedestrian_Impedence`=?, `SB_Pedestrian_Impedence`=?, "
					+ "`EB_Capacity`=?, `WB_Capacity`=?, `NB_Capacity`=?, `SB_Capacity`=?, "
					+ "`EB_Entry_Flow_Rate_Per_Hour`=?, `WB_Entry_Flow_Rate_Per_Hour`=?, `NB_Entry_Flow_Rate_Per_Hour`=?, `SB_Entry_Flow_Rate_Per_Hour`=?, "
					+ "`EB_Capacity_Bypass`=?, `WB_Capacity_Bypass`=?, `NB_Capacity_Bypass`=?, `SB_Capacity_Bypass`=?, "
					+ "`EB_Entry_Flow_Rate_Bypass_Per_Hour`=?, `WB_Entry_Flow_Rate_Bypass_Per_Hour`=?, `NB_Entry_Flow_Rate_Bypass_Per_Hour`=?, `SB_Entry_Flow_Rate_Bypass_Per_Hour`=?, "
					+ "`EB_Vol_To_Capacity_Ratio`=?, `WB_Vol_To_Capacity_Ratio`=?, `NB_Vol_To_Capacity_Ratio`=?, `SB_Vol_To_Capacity_Ratio`=?, "
					+ "`EB_Bypass_Vol_To_Capacity_Ratio`=?, `WB_Bypass_Vol_To_Capacity_Ratio`=?, `NB_Bypass_Vol_To_Capacity_Ratio`=?, `SB_Bypass_Vol_To_Capacity_Ratio`=?, "
					+ "`EB_Control_Delay`=?, `WB_Control_Delay`=?, `NB_Control_Delay`=?, `SB_Control_Delay`=?, "
					+ "`EB_Control_Delay_Bypass`=?, `WB_Control_Delay_Bypass`=?, `NB_Control_Delay_Bypass`=?, `SB_Control_Delay_Bypass`=?, "
					+ "`EB_LOS`=?, `WB_LOS`=?, `NB_LOS`=?, `SB_LOS`=?, "
					+ "`EB_LOS_Bypass`=?, `WB_LOS_Bypass`=?, `NB_LOS_Bypass`=?, `SB_LOS_Bypass`=?, "
					+ "`EB_Aggregate_Entry_Flow_Rate_Per_Hour`=?, `WB_Aggregate_Entry_Flow_Rate_Per_Hour`=?, `NB_Aggregate_Entry_Flow_Rate_Per_Hour`=?, `SB_Aggregate_Entry_Flow_Rate_Per_Hour`=?, "
					+ "`EB_Control_Delay_Aggregate`=?, `WB_Control_Delay_Aggregate`=?, `NB_Control_Delay_Aggregate`=?, `SB_Control_Delay_Aggregate`=?, "
					+ "`EB_Aggregate_Intersection`=?, `WB_Aggregate_Intersection`=?, `NB_Aggregate_Intersection`=?, `SB_Aggregate_Intersection`=?, "
					+ "`EB_Percentile_Queuing`=?, `WB_Percentile_Queuing`=?, `NB_Percentile_Queuing`=?, `SB_Percentile_Queuing`=?, "
					+ "`EB_Left_Lane_Capacity`=?, `WB_Left_Lane_Capacity`=?, `NB_Left_Lane_Capacity`=?, `SB_Left_Lane_Capacity`=?, " 
					+ "`EB_Right_Lane_Capacity`=?, `WB_Right_Lane_Capacity`=?, `NB_Right_Lane_Capacity`=?, `SB_Right_Lane_Capacity`=?, "
					+ "`EB_Left_Vol_To_Capacity_Ratio`=?, `WB_Left_Vol_To_Capacity_Ratio`=?, `NB_Left_Vol_To_Capacity_Ratio`=?, `SB_Left_Vol_To_Capacity_Ratio`=?, "
					+ "`EB_Right_Vol_To_Capacity_Ratio`=?, `WB_Right_Vol_To_Capacity_Ratio`=?, `NB_Right_Vol_To_Capacity_Ratio`=?, `SB_Right_Vol_To_Capacity_Ratio`=? "
					+ " where Number=?";
			
			PreparedStatement updateData = con.prepareStatement(query);
		
			while(rs.next())
			{
				
					volumes[0] = rs.getDouble(1);
					volumes[1] = rs.getDouble(2);
					volumes[2] = rs.getDouble(3);
					volumes[3] = rs.getDouble(4);
					volumes[4] = rs.getDouble(5);
					volumes[5] = rs.getDouble(6);
					volumes[6] = rs.getDouble(7);
					volumes[7] = rs.getDouble(8);
					volumes[8] = rs.getDouble(9);
					volumes[9] = rs.getDouble(10);
					volumes[10] = rs.getDouble(11);
					volumes[11] = rs.getDouble(12);
					volumes[12] = rs.getDouble(13);
					volumes[13] = rs.getDouble(14);
					volumes[14] = rs.getDouble(15);
					volumes[15] = rs.getDouble(16);
					volumes[16] = rs.getDouble(17);
					volumes[17] = rs.getDouble(18);
					volumes[18] = rs.getDouble(19);
					volumes[19] = rs.getDouble(20);
					volumes[20] = rs.getDouble(21);
					volumes[21] = rs.getDouble(22);
					volumes[22] = rs.getDouble(23);
					volumes[23] = rs.getDouble(24);
					volumes[24] = rs.getDouble(25);
					volumes[25] = rs.getDouble(26);
					volumes[26] = rs.getDouble(27);
					volumes[27] = rs.getDouble(28);
					volumes[28] = rs.getDouble(29);
					volumes[29] = rs.getDouble(30);
					volumes[30] = rs.getDouble(31);
					volumes[31] = rs.getDouble(32);
					volumes[32] = rs.getDouble(33);
					volumes[33] = rs.getDouble(34);
					volumes[34] = rs.getDouble(35);
					volumes[35] = rs.getDouble(36);
					
					
					if(volumes[0]>=firstindex && volumes[0]<=lastindex)
					{
					rbc=new RoundaboutComputeEngine(volumes);
					System.out.println("------------------------------------------ROW "+(j+1)+" -----------------------------------------------------------------------");
					ArrayList<RoundaboutBounds> allBounds=rbc.computeSteps();
					System.out.println("------------------------------------------END "+(j+1)+ " ----------------------------------------------------------------------");
					
					//Setting up all the values in database
					
					updateData.setString(1, "SB");
					updateData.setString(2, "NB");
					updateData.setString(3, "EB");
					updateData.setString(4, "WB");
					
					updateData.setString(5, "NB");
					updateData.setString(6, "SB");
					updateData.setString(7, "WB");
					updateData.setString(8, "NB");
	
					updateData.setString(9, "WB");
					updateData.setString(10, "EB");
					updateData.setString(11, "SB");
					updateData.setString(12, "NB");
					
					updateData.setDouble(13,allBounds.get(0).getVolumeFlowRateLeft());
					updateData.setDouble(14,allBounds.get(1).getVolumeFlowRateLeft());
					updateData.setDouble(15,allBounds.get(2).getVolumeFlowRateLeft());
					updateData.setDouble(16,allBounds.get(3).getVolumeFlowRateLeft());
					
					updateData.setDouble(17,allBounds.get(0).getVolumeFlowRateRight());
					updateData.setDouble(18,allBounds.get(1).getVolumeFlowRateRight());
					updateData.setDouble(19,allBounds.get(2).getVolumeFlowRateRight());
					updateData.setDouble(20,allBounds.get(3).getVolumeFlowRateRight());
					
					updateData.setDouble(21,allBounds.get(0).getVolumeFlowRateUTurn());
					updateData.setDouble(22,allBounds.get(1).getVolumeFlowRateUTurn());
					updateData.setDouble(23,allBounds.get(2).getVolumeFlowRateUTurn());
					updateData.setDouble(24,allBounds.get(3).getVolumeFlowRateUTurn());
					
					updateData.setDouble(25,allBounds.get(0).getVolumeFlowRateThrough());
					updateData.setDouble(26,allBounds.get(1).getVolumeFlowRateThrough());
					updateData.setDouble(27,allBounds.get(2).getVolumeFlowRateThrough());
					updateData.setDouble(28,allBounds.get(3).getVolumeFlowRateThrough());
					
					updateData.setDouble(29,allBounds.get(0).getHeavyVehicleAdjustmentFactor());
					updateData.setDouble(30,allBounds.get(1).getHeavyVehicleAdjustmentFactor());
					updateData.setDouble(31,allBounds.get(2).getHeavyVehicleAdjustmentFactor());
					updateData.setDouble(32,allBounds.get(3).getHeavyVehicleAdjustmentFactor());
					
					updateData.setDouble(33,allBounds.get(0).getDemandFlowRateForMovementLeft());
					updateData.setDouble(34,allBounds.get(1).getDemandFlowRateForMovementLeft());
					updateData.setDouble(35,allBounds.get(2).getDemandFlowRateForMovementLeft());
					updateData.setDouble(36,allBounds.get(3).getDemandFlowRateForMovementLeft());
				
					updateData.setDouble(37,allBounds.get(0).getDemandFlowRateForMovementRight());
					updateData.setDouble(38,allBounds.get(1).getDemandFlowRateForMovementRight());
					updateData.setDouble(39,allBounds.get(2).getDemandFlowRateForMovementRight());
					updateData.setDouble(40,allBounds.get(3).getDemandFlowRateForMovementRight());
					
					updateData.setDouble(41,allBounds.get(0).getDemandFlowRateForMovementUTurn());
					updateData.setDouble(42,allBounds.get(1).getDemandFlowRateForMovementUTurn());
					updateData.setDouble(43,allBounds.get(2).getDemandFlowRateForMovementUTurn());
					updateData.setDouble(44,allBounds.get(3).getDemandFlowRateForMovementUTurn());
					
					updateData.setDouble(45,allBounds.get(0).getDemandFlowRateForMovementThrough());
					updateData.setDouble(46,allBounds.get(1).getDemandFlowRateForMovementThrough());
					updateData.setDouble(47,allBounds.get(2).getDemandFlowRateForMovementThrough());
					updateData.setDouble(48,allBounds.get(3).getDemandFlowRateForMovementThrough());
					
					updateData.setDouble(49,allBounds.get(0).getCirculatingFlowRates());
					updateData.setDouble(50,allBounds.get(1).getCirculatingFlowRates());
					updateData.setDouble(51,allBounds.get(2).getCirculatingFlowRates());
					updateData.setDouble(52,allBounds.get(3).getCirculatingFlowRates());
					
					updateData.setDouble(53,allBounds.get(0).getExitingFlowRates());
					updateData.setDouble(54,allBounds.get(1).getExitingFlowRates());
					updateData.setDouble(55,allBounds.get(2).getExitingFlowRates());
					updateData.setDouble(56,allBounds.get(3).getExitingFlowRates());
					
					updateData.setDouble(57,allBounds.get(0).getEntryFlowRates());
					updateData.setDouble(58,allBounds.get(1).getEntryFlowRates());
					updateData.setDouble(59,allBounds.get(2).getEntryFlowRates());
					updateData.setDouble(60,allBounds.get(3).getEntryFlowRates());
					
					updateData.setDouble(61,allBounds.get(0).getCapcaityEntryLane());
					updateData.setDouble(62,allBounds.get(1).getCapcaityEntryLane());
					updateData.setDouble(63,allBounds.get(2).getCapcaityEntryLane());
					updateData.setDouble(64,allBounds.get(3).getCapcaityEntryLane());
					
					updateData.setDouble(65,allBounds.get(0).getCapacityByPassLane());
					updateData.setDouble(66,allBounds.get(1).getCapacityByPassLane());
					updateData.setDouble(67,allBounds.get(2).getCapacityByPassLane());
					updateData.setDouble(68,allBounds.get(3).getCapacityByPassLane());
				
					updateData.setDouble(69,allBounds.get(0).getPedestrianImpedence());
					updateData.setDouble(70,allBounds.get(1).getPedestrianImpedence());
					updateData.setDouble(71,allBounds.get(2).getPedestrianImpedence());
					updateData.setDouble(72,allBounds.get(3).getPedestrianImpedence());
					
					updateData.setDouble(73,allBounds.get(0).getCapacity());
					updateData.setDouble(74,allBounds.get(1).getCapacity());
					updateData.setDouble(75,allBounds.get(2).getCapacity());
					updateData.setDouble(76,allBounds.get(3).getCapacity());
					
					updateData.setDouble(77,allBounds.get(0).getEntryFlowRatesPerHour());
					updateData.setDouble(78,allBounds.get(1).getEntryFlowRatesPerHour());
					updateData.setDouble(79,allBounds.get(2).getEntryFlowRatesPerHour());
					updateData.setDouble(80,allBounds.get(3).getEntryFlowRatesPerHour());
					
					updateData.setDouble(81,allBounds.get(0).getCapacityByPass());
					updateData.setDouble(82,allBounds.get(1).getCapacityByPass());
					updateData.setDouble(83,allBounds.get(2).getCapacityByPass());
					updateData.setDouble(84,allBounds.get(3).getCapacityByPass());
					
					updateData.setDouble(85,allBounds.get(0).getEntryFlowRatesByPassPerHour());
					updateData.setDouble(86,allBounds.get(1).getEntryFlowRatesByPassPerHour());
					updateData.setDouble(87,allBounds.get(2).getEntryFlowRatesByPassPerHour());
					updateData.setDouble(88,allBounds.get(3).getEntryFlowRatesByPassPerHour());
					
					updateData.setDouble(89,allBounds.get(0).getVolumeToCapcityRatio());
					updateData.setDouble(90,allBounds.get(1).getVolumeToCapcityRatio());
					updateData.setDouble(91,allBounds.get(2).getVolumeToCapcityRatio());
					updateData.setDouble(92,allBounds.get(3).getVolumeToCapcityRatio());
					
					updateData.setDouble(93,allBounds.get(0).getVolumeToCapcityRatioByPass());
					updateData.setDouble(94,allBounds.get(1).getVolumeToCapcityRatioByPass());
					updateData.setDouble(95,allBounds.get(2).getVolumeToCapcityRatioByPass());
					updateData.setDouble(96,allBounds.get(3).getVolumeToCapcityRatioByPass());
			
					updateData.setDouble(97,allBounds.get(0).getControlDelay());
					updateData.setDouble(98,allBounds.get(1).getControlDelay());
					updateData.setDouble(99,allBounds.get(2).getControlDelay());
					updateData.setDouble(100,allBounds.get(3).getControlDelay());
					
					updateData.setDouble(101,allBounds.get(0).getControlDelayByPass());
					updateData.setDouble(102,allBounds.get(1).getControlDelayByPass());
					updateData.setDouble(103,allBounds.get(2).getControlDelayByPass());
					updateData.setDouble(104,allBounds.get(3).getControlDelayByPass());
					
					updateData.setDouble(105,allBounds.get(0).getLos());
					updateData.setDouble(106,allBounds.get(1).getLos());
					updateData.setDouble(107,allBounds.get(2).getLos());
					updateData.setDouble(108,allBounds.get(3).getLos());
	
					updateData.setDouble(109,allBounds.get(0).getLosByPass());
					updateData.setDouble(110,allBounds.get(1).getLosByPass());
					updateData.setDouble(111,allBounds.get(2).getLosByPass());
					updateData.setDouble(112,allBounds.get(3).getLosByPass());
					
					updateData.setDouble(113,allBounds.get(0).getAggregateEntryFlowRatesPerHour());
					updateData.setDouble(114,allBounds.get(1).getAggregateEntryFlowRatesPerHour());
					updateData.setDouble(115,allBounds.get(2).getAggregateEntryFlowRatesPerHour());
					updateData.setDouble(116,allBounds.get(3).getAggregateEntryFlowRatesPerHour());
					
					updateData.setDouble(117,allBounds.get(0).getControlDelayAggregate());
					updateData.setDouble(118,allBounds.get(1).getControlDelayAggregate());
					updateData.setDouble(119,allBounds.get(2).getControlDelayAggregate());
					updateData.setDouble(120,allBounds.get(3).getControlDelayAggregate());
					
					updateData.setDouble(121,rbc.calculateAggregateIntersection());
					updateData.setDouble(122,rbc.calculateAggregateIntersection());
					updateData.setDouble(123,rbc.calculateAggregateIntersection());
					updateData.setDouble(124,rbc.calculateAggregateIntersection());
	
					updateData.setDouble(125,allBounds.get(0).getPercentile());
					updateData.setDouble(126,allBounds.get(1).getPercentile());
					updateData.setDouble(127,allBounds.get(2).getPercentile());
					updateData.setDouble(128,allBounds.get(3).getPercentile());
					
					updateData.setDouble(129,allBounds.get(0).getCapacityofLeftLane());
					updateData.setDouble(130,allBounds.get(1).getCapacityofLeftLane());
					updateData.setDouble(131,allBounds.get(2).getCapacityofLeftLane());
					updateData.setDouble(132,allBounds.get(3).getCapacityofLeftLane());
					
					updateData.setDouble(133,allBounds.get(0).getCapacityofRightLane());
					updateData.setDouble(134,allBounds.get(1).getCapacityofRightLane());
					updateData.setDouble(135,allBounds.get(2).getCapacityofRightLane());
					updateData.setDouble(136,allBounds.get(3).getCapacityofRightLane());
					
					updateData.setDouble(137,allBounds.get(0).getVtocLeftLane());
					updateData.setDouble(138,allBounds.get(1).getVtocLeftLane());
					updateData.setDouble(139,allBounds.get(2).getVtocLeftLane());
					updateData.setDouble(140,allBounds.get(3).getVtocLeftLane());
					
					updateData.setDouble(141,allBounds.get(0).getVtocRightLane());
					updateData.setDouble(142,allBounds.get(1).getVtocRightLane());
					updateData.setDouble(143,allBounds.get(2).getVtocRightLane());
					updateData.setDouble(144,allBounds.get(3).getVtocRightLane());
					updateData.setDouble(145,j+1);
					updateData.executeUpdate();
					j++;
				}
			}
			
			long endTime= System.currentTimeMillis();
			System.out.println("Time taken: "+(endTime-startTime));
			
		con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}