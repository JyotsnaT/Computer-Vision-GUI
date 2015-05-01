import matlabcontrol.*;

public class InvokeMatlab {

public static void main(String[] args)
throws MatlabConnectionException, MatlabInvocationException
{
// create proxy
MatlabProxyFactoryOptions options =
new MatlabProxyFactoryOptions.Builder()
.setUsePreviouslyControlledSession(true).setHidden(true)
.build();
RemoteMatlabProxyFactory factory = new RemoteMatlabProxyFactory(options);
RemoteMatlabProxy proxy = (RemoteMatlabProxy) factory.getProxy();
//extra


// call builtin function
proxy.eval("sqrt(4)");
proxy.eval("vid_obj = VideoReader('/home/jyotsna/Downloads/hollywood2_actioncliptrain00001_h264.mp4')");
proxy.eval("B = vid_obj.Height");
proxy.eval("X = vid_obj.Duration");
double result1 = ((double[]) proxy.getVariable("B"))[0];
double result2 = ((double[])proxy.getVariable("X"))[0];
Object[] retunArgs = proxy.returningEval("B", 1);
//System.out.println(B);
System.out.println("Height,duration: " + result1 +"; "+ result2);
System.out.println("video object:" + (double[])retunArgs[0]);
// call user-defined function (must be on the path)
//proxy.eval(“addpath(‘F:\\Matlab’)”);
//proxy.feval(“Sample”);
//proxy.eval(“rmpath(‘F:\\Matlab’)”);

//Set a variable, add to it, retrieve it, and print the result
proxy.setVariable("a", 5);
proxy.eval("a = a + 6");
double result = ((double[]) proxy.getVariable("a"))[0];
System.out.println("Result: " + result);
proxy.exit();

// close connection
proxy.disconnect();

}
}