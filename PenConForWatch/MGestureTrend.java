	/**
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @date：2019-1-1 下午1:27:45
	 */
	private class MGestureTrend implements Serializable{//内部类，和 MGesture共用一些变量，从而保证两者变量的一致性
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/*byte是一个8bit的有符号数，这里规定一个byte的8个bit的编号从右到左依次为0~7
		 * 因为x，y,z的变化趋势只有三种，增大，减小，不变化，即3个状态，因此，只需两个bit就可以表示
		 * 这里规定：
		 * x变化趋势用编号为0和1的两个bit表示
		 * y变化趋势用编号为2和3的两个bit表示
		 * z变化趋势用编号为4和5的两个bit表示
		 *  编号为6的bit位空闲备做他用
		 * */
		private byte[] gesturesTrend;//表示手势变化趋势的byte数组，new byte[x]，这里的x确定了最多可以支持手势变化次数
		private int[] trendChangePlaceIndex;
//		private ArrayList<Byte> gesturesTrend=new ArrayList<Byte>();
   
		/*
		 * 初始化代码块，在构造函数之前运行
		 */
		{
		trendChangePlaceIndex=new int[gesturesTrendCount];
		gesturesTrend=new byte[gesturesTrendCount];
		for (byte  element:gesturesTrend) {
			element=0;
		}
		}
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-1 下午5:03:26
 * @param mGesturePoints
 */
		private void   getTrends(ArrayList<MGesturePoint> mGesturePoints){
			
			if (mGesturePoints.size()<2) {
				return ;
			}
			MGesturePoint lastMGesturePoint=mGesturePoints.get(0);
			MGesturePoint currentMGesturePoint;
			GestureTrend[] tempTrend=new GestureTrend[3];
			GestureTrend[] currentTrend=new GestureTrend[3];
			currentTrend[0]=currentTrend[1]=currentTrend[2]=null;
			
//			int trendChangeCount=0;
			for (int i = 1; i < mGesturePoints.size(); i++) {
				currentMGesturePoint=mGesturePoints.get(i);

					tempTrend[0]=getTrend(lastMGesturePoint.x, currentMGesturePoint.x);
					tempTrend[1]=getTrend(lastMGesturePoint.y, currentMGesturePoint.y);
					if (index==0) {
						currentTrend[0]=tempTrend[0];//x
						currentTrend[1]=tempTrend[1];//y
						setGestureTrend(CoordinateDimension.X,currentTrend[0]);
						setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
						trendChangePlaceIndex[index]=i;
						if (index<gesturesTrend.length) {
							index++;
						}
						continue;
					}else {
						if (!tempTrend[0].equals(currentTrend[0])&&!tempTrend[1].equals(currentTrend[1])) {//x和y的变化趋势都改变
							currentTrend[0]=tempTrend[0];//x
							currentTrend[1]=tempTrend[1];//y
							setGestureTrend(CoordinateDimension.X,currentTrend[0]);
							setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
							trendChangePlaceIndex[index]=i;
							if (index<gesturesTrend.length) {
								index++;
							}
							continue;	
						}
						if (!tempTrend[0].equals(currentTrend[0])&&tempTrend[1].equals(currentTrend[1])) {//x趋势改变，y趋势没有改变
							currentTrend[0]=tempTrend[0];//x
//							currentTrend[1]=tempTrend[1];//y不用更新
							setGestureTrend(CoordinateDimension.X,currentTrend[0]);
							setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
							trendChangePlaceIndex[index]=i;
							if (index<gesturesTrend.length) {
								index++;
							}
							continue;	
						}
						if (tempTrend[0].equals(currentTrend[0])&&!tempTrend[1].equals(currentTrend[1])) {//y趋势改变，x趋势没有改变
//							currentTrend[0]=tempTrend[0];//x不用更新
							currentTrend[1]=tempTrend[1];//y
							setGestureTrend(CoordinateDimension.X,currentTrend[0]);
							setGestureTrend(CoordinateDimension.Y,currentTrend[1]);
							trendChangePlaceIndex[index]=i;
							if (index<gesturesTrend.length) {
								index++;
							}
							continue;	
						}
					}
			}
			
		}	
/**
 * 
 * @author： nkxm
 * @name:  
 * @description ：
 * @parameter:
 * @parameter:
 * @return:
 * @date：2019-1-1 下午5:03:50
 * @param last
 * @param current
 * @return
 */
		private GestureTrend getTrend(float last,float current){
			GestureTrend gestureTrend=null;
			float delt=3;
			if (current-last>delt) {//增加的趋势
				gestureTrend=GestureTrend.increase;
				return gestureTrend;
			}
			if (current-last<-delt) {//减少的趋势
				gestureTrend=GestureTrend.decrease;
				return gestureTrend;
			}
			if (current-last>-delt&&current-last<delt) {//不变的趋势
				gestureTrend=GestureTrend.even;
				return gestureTrend;
			}
			return gestureTrend;
		}
	/**
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2018-12-28 上午10:40:18
	 * @param cD
	 * @param trend
	 * @param index
	 */
		 public void  setGestureTrend(CoordinateDimension cD,GestureTrend trend){
			 if (index>=gesturesTrend.length||index<0) {
				 Log.e("setGestureTrend", new Date().getTime()+"--setGesturesTrend:index的值超越了gestureTrend的长度，正确的长度为0~"+gesturesTrend.length);
				return;
			}
			 switch (cD.getValue()) {
			case X_DIMENSION:
				gesturesTrend[index]=trend.getValue();
				break;
			case Y_DIMENSION:
				gesturesTrend[index]=(byte) (trend.getValue()<<2);
				break;
			case Z_DIMENSION:
				gesturesTrend[index]=(byte) (trend.getValue()<<4);
				break;
			default:
				Log.e("setGestureTrend", new Date().getTime()+"--setGesturesTrend:输入的坐标系维度不存在，正确的只有三种：X_DIMENSION，Y_DIMENSION，Z_DIMENSION");
				break;
			}
			 
		 }
	/**
	 *	 
	 * @author： nkxm
	 * @name:  
	 * @description ：
	 * @parameter:
	 * @parameter:
	 * @return:
	 * @date：2018-12-28 上午10:50:59
	 * @param cD
	 * @param trends
	 * @return
	 */
		 public byte getCoordinateDimensionTrend(CoordinateDimension cD, byte CoordinateTrends){
			 
			 switch (cD.getValue()) {
			case X_DIMENSION:
				CoordinateTrends=(byte) ((byte)3&CoordinateTrends);
				break;
			case Y_DIMENSION:
				CoordinateTrends=(byte) ((byte)12&CoordinateTrends);
				break;
			case Z_DIMENSION:
				CoordinateTrends=(byte) ((byte)48&CoordinateTrends);
				break;
			default:
				Log.e("getCoordinateDimensionTrend", new Date().getTime()+"--getCoordinateDimensionTrend:输入的坐标系维度不存在，正确的只有三种：X_DIMENSION，Y_DIMENSION，Z_DIMENSION");
				break;
			}
			 
			return CoordinateTrends;
			 
		 }
	
	}
