/*import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.Arrays;
public class WordCount {   
    public static void wordCountJava8( String filename ){
        SparkConf conf = new SparkConf().setMaster("local").setAppName("WordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> input = sc.textFile( filename );
        JavaRDD<String> words = input.flatMap( s -> Arrays.asList( s.split( " " ) ).iterator() );
        JavaPairRDD<String, Integer> counts = words.mapToPair( t -> new Tuple2( t, 1 ) ).reduceByKey( (x, y) -> (int)x + (int)y );
        counts.saveAsTextFile( "output1" );
    }
    public static void main( String[] args )
    {
        wordCountJava8("hello");
    }
}
*/