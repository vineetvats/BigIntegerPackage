package aab180004;

/**
 * Num class which stores and performs arithmetic operations on arbitrarily large integers
 * @author Achyut Arun Bhandiwad - AAB180004
 * @author Nirbhay Sibal - NXS180002
 * @author Vineet Vats - VXV180008
 * @version 1.80
 * @since 1.0
 */

import java.util.ArrayDeque;
import java.util.Arrays;
import java.lang.Math;

public class Num  implements Comparable<Num> {

    static long defaultBase = 10;
    long base = 1000000000;
    long[] arr;  // array to store arbitrarily large integers
    boolean isNegative;  // boolean flag to represent negative numbers
    int len;  // actual number of elements of array that are used;  number is stored in arr[0..len-1]

    /**
     * Constructor for Num class; takes a string s as parameter, with a number in decimal, and creates the Num object representing that number in the chosen base. Note that, the string s is in base 10, even if the chosen base is not 10. The string s can have arbitrary length.
     * @param s
     */
    public Num(String s) {
        this.base = defaultBase; //input is in base 10
        len = s.length();  //initial lenght
        int i=0,j;
        if(s.charAt(0) == '-'){ //check if number is negetive
            isNegative = true;
            i++;
            len--;
        }
        else{
            isNegative = false;
        }
        arr = new long[len];
        for(j=len-1;j>=0;j--,i++){
            arr[j] = Character.digit(s.charAt(i),10); // storing character by character into the array
        }

        arr = convertBase(arr,10,1000000000); // converting the number to current base
        this.base = 1000000000;
        this.len = arr.length;
    }

    /**
     * Default constructor
     */
    public Num(){
        len = 0;
        arr = new long[1000];
    }

    /**
     * Constructor which takes long array as input and created object
     * @param arr
     */
    public Num(long arr[]){
        this(arr,1000000000);
    }

    /**
     * Constructor which takes long array and desired base to create object
     * @param arr
     * @param base
     */
    public Num(long arr[], long base){
        this.arr = arr;
        this.len = arr.length;
        this.base = base;
    }

    /**
     * Constructor which takes long number to create object in current base
     * @param x
     */
    public Num(long x) {
        this(x,1000000000);
    }

    /**
     * Constructor which takes long number and desired base to create object in desired base
     * @param x
     * @param base
     */
    public Num(long x, long base) {
        this();
        this.base = base;
        int i = 0;
        if(x==0){
            arr[i] = 0;
            i++;
        }
        if(x<0){
            isNegative = true;
            x *=-1;
        }
        while(x>0)
        {
            isNegative = false;
            arr[i] = x % base;
            x = x / base;
            i++;
        }
        len = i;
    }

    /**
     * Sum of two numbers a+b stored as Num
     * @param a
     * @param b
     * @return Sum
     */
    public static Num add(Num a, Num b) {
        if(a.base != b.base)
            throw new NumberFormatException();
        long[] out = new long[Math.max(a.len,b.len)+1];
        long sum = 0;
        long carry = 0;
        int i=0;
        while(i<a.len && i<b.len)
        {
            sum = a.arr[i] + b.arr[i] + carry;
            out[i] =sum % a.base;
            carry = sum /a.base;
            i++;
        }
        while(i<a.len)
        {
            sum = a.arr[i] + carry;
            out[i] = sum % a.base;
            carry = sum / a.base;
            i++;
        }
        while(i<b.len)
        {
            sum = b.arr[i] + carry;
            out[i] = sum % b.base;
            carry = sum / a.base;
            i++;
        }
        if(carry>0)
            out[i] = carry;

        //Remove trailing zero
        return new Num(out[out.length-1]==0?Arrays.copyOfRange(out,0,out.length-1):out,a.base);
    }

    /**
     * Difference of a and b (a-b)
     * @param a
     * @param b
     * @return Difference
     */
    public static Num subtract(Num a, Num b) {
        if(a.base != b.base)
            throw new NumberFormatException();
        long carry = 0;
        Num zero = new Num(0);
        long[] diff = new long[Math.max(a.len,b.len)]; // Max length will be max of length of both the number
        Num x =a ,y =b;
        if(a.compareTo(b)<0)
        {
            x = b;
            y = a;
        }
        if(x.compareTo(zero) ==0)
            return y;
        if(y.compareTo(zero)==0)
            return x;

        for(int i=0; i<y.len; i++)
        {
            long sub = x.arr[i] - y.arr[i] - carry;
            if(sub < 0){
                sub += x.base;
                carry = 1;
            }
            else{
                carry = 0;
            }
            diff[i] = sub;
        }
        for(int j=y.len; j<x.len; j++)
        {
            long sub = x.arr[j] - carry;
            if(sub < 0){
                sub += x.base;
                carry = 1;
            }
            else{
                carry = 0;
            }
            diff[j] = sub;
        }

        //Removing trailing zeros
        int k = diff.length-1;
        while(k>=0 && diff[k] == 0)
            k--;
        if(k == -1)
            return new Num(0,a.base);
        if(k == 0)
            return  new Num(diff[0],a.base);

        Num output = new Num(Arrays.copyOfRange(diff,0,k+1),a.base);

        //Check if number is negetive
        if(a.compareTo(b)<0)
        {
            output.isNegative = true;
        }
        return output;
    }

    /**
     * Product of two numbers a*b.
     * @param a
     * @param b
     * @return Prodcut
     */
    public static Num product(Num a, Num b) {
        if(a.base != b.base)
            throw new NumberFormatException();
        long[] product = new long[a.len+b.len]; //Max lenght can be sum of the lengths
        Num zero = new Num(0,a.base);

        //best case
        if(a.compareTo(zero)==0 || b.compareTo(zero)==0)
            return zero;

        long carry;
        for(int i=0; i<b.len ; i++){
            carry=0;
            for(int j=0; j<a.len ; j++){
                product[i+j] += carry + a.arr[j] * b.arr[i];
                carry = product[i+j] / a.base;
                product[i+j] = product[i+j] % a.base;
            }
            product[i + a.len] = carry;
        }

        //Removing trailing zeros
        Num output;
        if(product[product.length-1]==0)
            output = new Num(Arrays.copyOfRange(product,0,product.length-1),a.base);
        else
            output = new Num(product,a.base);

        //Checking if the product is negetive
        if(a.isNegative ^ b.isNegative)
            output.isNegative = true;
        else
            output.isNegative = false;

        return output;
    }

    /**
     * given an Num x, and n, returns the Num corresponding to x^n (x to the power n). Assuming that n is a non negative number. Using divide-and-conquer to implement power using O(log n) calls to product and add.
     * @param a
     * @param n
     * @return a^n
     */
    public static Num power(Num a, long n) {
        if( n == 0)
            return new Num(1,a.base);
        else if(n % 2 == 0 )
            return product(power(a,n/2),power (a,n/2));
        else
            return product(a,product(power(a,n/2),power (a,n/2)));
    }

    /**
     * Integer division a/b. Using divide-and-conquer or division algorithm. Return null if b=0.
     * @param a
     * @param b
     * @return a/b, null if b=0
     */
    public static Num divide(Num a, Num b) {
        if(a.base != b.base)
            throw new NumberFormatException();
        if(b.compareTo(new Num(0,b.base)) ==0)
            return null;
        Num left = new Num(0,a.base);
        Num right = new Num (Arrays.copyOf(a.arr,a.len),a.base);
        Num prevMid = new Num (0,a.base);

        Num temp1,temp2;

        while(true){
            temp1 = subtract(right,left); //using temporary variables to store intermediate results
            temp2 = temp1.by2();
            Num mid = add(left,temp2);
            if(product(b,mid).compareTo(a) == 0 || prevMid.compareTo(mid)==0)
                return mid;
            if(product(b,mid).compareTo(a) < 0)
                left = mid;
            else
                right = mid;
            prevMid =mid;
        }
    }

    /**
     * Remainder you get when a is divided by b (a%b). Assuming that a is non-negative, and b > 0. Return null if b=0.
     * @param a
     * @param b
     * @return remainder of a/b
     */
    public static Num mod(Num a, Num b) {
        if(a.base != b.base)
            throw new NumberFormatException();
        Num ZERO = new Num(0,a.base);
        Num ONE = new Num(1,a.base);
        if(b.compareTo(ZERO) ==0)
            return null;
        if(a.compareTo(b) == 0 )
            return ZERO;
        if(b.compareTo(ONE) == 0)
            return ZERO;

        //using temp variables to store intermediate results for readability
        Num temp1 = divide(a,b);
        Num temp2 = product(b,temp1);
        Num temp3 = subtract(a,temp2);

        return (temp3);
    }

    /**
     * Return the square root of a (truncated). Use binary search. Assuming that a is non-negative
     * @param a
     * @return square root of a
     */
    public static Num squareRoot(Num a) {
        Num ZERO = new Num(0,a.base);
        Num ONE = new Num(1,a.base);
        if(a.compareTo(ZERO)==0 || a.compareTo(ONE)==0)
            return a;

        Num start = ONE;
        Num end = a;
        Num result = ZERO;
        Num prevMid = ZERO;

        while(start.compareTo(end) <=0){
            Num mid = (add(start,end)).by2();

            Num prod = product(mid,mid);
            if(prod.compareTo(a)== 0 || prevMid.compareTo(mid) == 0 )
                return  mid;

            if(prod.compareTo(a)<0){
                start = add(mid,ONE);
                result = mid;
            }
            else{
                end = subtract(mid,ONE);
            }
            prevMid = mid;
        }
        return result;
    }


    /**
     * compare "this" to "other": return +1 if this is greater, 0 if equal, -1 otherwise
     * @param other
     * @return +1 if this is greater, 0 if equal, -1 otherwise
     */
    public int compareTo(Num other) {
        if(!this.isNegative && !other.isNegative)
            return unsignedCompareTo(other);
        else if(this.isNegative && other.isNegative)
            return -1*unsignedCompareTo(other);
        else if (this.isNegative && !other.isNegative)
            return -1;
        else
            return 1;
    }

    /**
     * Unsigned compareTo
     * @param other
     * @return
     */
    public int unsignedCompareTo(Num other) {
        if (this.len<other.len) {
            return -1;
        } else if (this.len>other.len) {
            return +1;
        } else {
            return compareMagnitude(other);
        }
    }

    /**
     * Comparing just the magnitude
     * @param other
     * @return
     */
    public int compareMagnitude(Num other) {
        for (int i = this.len - 1; i >= 0; i--) {
            if (this.arr[i] < other.arr[i]) {
                return -1;
            } else if (this.arr[i] > other.arr[i]) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Prints the list in the format "base: elements of list ..."
     * For example, if base=100, and the number stored corresponds to 10965,
     * then the output is "100: 65 9 1"
     */
    public void printList() {
        StringBuilder output = new StringBuilder();
        output.append(base+":");
        if(isNegative)
            output.append(" -");
        for(int i =0; i<len; i++){
            output.append(" "+arr[i]);
        }
        System.out.print(output);
    }

    /**
     * @return base
     */
    public long base() { return base; }

    /**
     * Return number equal to "this" number, in base=newBase
     * @param newBase
     * @return number equal to "this" number, in base=newBase
     */
    public Num convertBase(int newBase) {
        Num thisNum = new Num(this.arr);
        long[] newNum = convertBase(thisNum.arr,(int)thisNum.base,newBase);
        Num result = new Num(newNum,newBase);
        return result;
    }

    /**
     * ToString witout base change.
     * @return
     */
    public String toStringWithoutBaseChange() {
        StringBuilder output = new StringBuilder();
        for(int i=len-1; i>=0; i--)
            output.append(arr[i]);
        return String.valueOf(output);
    }

    /**
     * return string in base 10
     * @return number in base 10
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        for(int i=len-1; i>=0; i--)
            output.append(arr[i]);
        return String.valueOf(output);
    }

    /**
     * Convert base helped function
     * @param thisNumArr
     * @param currentBase
     * @param newBase
     * @return array with new base
     */
    public long[] convertBase(long[] thisNumArr, int currentBase,int newBase){
        Num ZERO = new Num(0,currentBase);
        int arrSize = 0;
        Num thisNum = new Num(thisNumArr,currentBase);
        Num b = new Num(newBase,currentBase);
        arrSize = (int) Math.ceil((thisNum.len+1)/Math.log10(newBase)+1);
        long[] newNum = new long[1000];
        int i =0;

        //horner's method
        while(thisNum.compareTo(ZERO) > 0){
            newNum[i] = Long.parseLong(mod(thisNum,b).toStringWithoutBaseChange());
            thisNum = divide(thisNum,b);
            i++;
        }

        //removing trailing zeros
        int k = newNum.length -1;
        while(k>=0 && newNum[k] == 0)
            k--;
        if(k == -1)
            return new long[]{0};
        if(k == 0)
            return new long[]{newNum[0]};

        return Arrays.copyOfRange(newNum,0,k+1);
    }

    /**
     * Function to convert base to 10
     * @return  new number in base 10
     */
    public Num convertBaseTo10() {
        Num ZERO = new Num(0,10);
        Num thisNum = new Num(this.arr,10);
        int arrSize = 0;
        arrSize = (int) Math.ceil((len+1)/Math.log10(10)+1);
        Num newNum = ZERO;
        for(int i=0 ; i< thisNum.len ; i++){
            long temp =(long)(thisNum.arr[i] * Math.pow(base, i));
            newNum = add(newNum,new Num(temp,10));
        }
        return newNum;
    }

    /**
     * Divide by 2, for using in binary search
     * @return
     */
    public Num by2() {
        Num ZERO = new Num(0,this.base);
        Num one = new Num(1,this.base);
        if(this.compareTo(one)==0 || this.compareTo(ZERO) == 0)
            return ZERO;
        long[] output = Arrays.copyOf(this.arr,this.len);
        long carry = 0;
        for(int i=len-1; i>=0 ; i-- ){
            output[i] = output[i] + carry;
            if(output[i] % 2 == 1)
                carry = this.base;
            else
                carry = 0;
            output[i] = output[i] / 2;
        }

        //Removing trailing zeros
        Num by2;
        if(output[output.length-1]==0) {
            by2 = new Num(Arrays.copyOfRange(output, 0, output.length - 1),this.base);
        }
        else
            by2 = new Num(output,this.base);
        return  by2;
    }

    /**
     * Evaluate an expression in postfix and return resulting number
     * Each string is one of: "*", "+", "-", "/", "%", "^", "0", or
     * a number: [1-9][0-9]*.  There is no unary minus operator.
     * @param expr
     * @return
     */
    public static Num evaluatePostfix(String[] expr) {
        ArrayDeque<Num> stack = new ArrayDeque<>();

        for(int i=0 ; i<expr.length ; i++){
            if(Character.isDigit(expr[i].charAt(0))){
                stack.push(new Num(expr[i]));
            }
            else{
                Num val1 = stack.pop();
                Num val2 = stack.pop();

                switch(expr[i]){
                    case "+":
                        stack.push(add(val2,val1));
                        break;

                    case "-":
                        stack.push(subtract(val2,val1));
                        break;

                    case "/":
                        stack.push(divide(val2,val1));
                        break;

                    case "*":
                        stack.push(product(val2,val1));
                        break;

                    case "%":
                        stack.push(mod(val2,val1));
                        break;

                    case "^":
                        stack.push(power(val2,Long.parseLong(val1.toString())));
                        break;

                }

            }

        }

        return stack.pop();
    }


    /**
     * Evaluate an expression in infix and return resulting number
     * Each string is one of: "*", "+", "-", "/", "%", "^", "(", ")", "0", or
     * a number: [1-9][0-9]*.  There is no unary minus operator.
     * @param expr
     * @return result of expression
     */
    public static Num evaluateInfix(String[] expr) {

        ArrayDeque<Num> values = new ArrayDeque<>(); //using array deque to use as a stack
        ArrayDeque<String> operators = new ArrayDeque<>();

        for(int i=0 ; i<expr.length; i++){
            if(Character.isDigit(expr[i].charAt(0))){
                values.push(new Num(expr[i]));
            }
            else if(expr[i].equals("(")){
                operators.push(expr[i]);
            }
            else if(expr[i].equals(")")){
                while(!operators.peek().equals("(")){
                    values.push(evaluate(operators.pop(),values.pop(),values.pop()));
                }
                operators.pop();
            }else{
                while(!operators.isEmpty() && hasPrecedence(expr[i],operators.peek())){
                    values.push(evaluate(operators.pop(),values.pop(),values.pop()));
                }
                operators.push(expr[i]);
            }
        }

        while (!operators.isEmpty()){
            values.push(evaluate(operators.pop(),values.pop(),values.pop()));
        }

        return values.pop();
    }

    /**
     * Helper method for evaluateInfix.
     * Checks for the precedence of two operators
     * @param operator1
     * @param operator2
     * @return
     */
    public static boolean hasPrecedence(String operator1, String operator2){
        if(operator2.equals("(") || operator2.equals(")")){
            return false;
        }
        if((operator1.equals("*") || operator1.equals("/")) && (operator2.equals("+") || operator2.equals("-"))){
            return false;
        }else{
            return true;
        }
    }

    /**
     * Helper method for evaluateInfix.
     * Evaluates the expression of the form : b operator a
     * @param operator
     * @param a
     * @param b
     * @return b operator a
     */
    public static Num evaluate(String operator,Num a,Num b){
        switch(operator){
            case "+":
                return add(b,a);

            case "-":
                return (subtract(b,a));

            case "/":
                return  (divide(b,a));

            case "*":
                return  (product(b,a));

            case "%":
                return (mod(b,a));

            case "^":
                return  (power(b,Long.parseLong(a.toString())));

        }
        return new Num(0);
    }


    public static void main(String[] args) {
        Num s = new Num("1000000000000000000000");
        Num t = new Num(11);

        //String[] input = {"10","+","2","*","6"};
        String[] input = { "98765432109876543210987654321",  "5432109876543210987654321", "345678901234567890123456789012", "*", "+", "246801357924680135792468013579", "*", "12345678910111213141516171819202122", "191817161514131211109876543210", "13579", "24680", "*", "-", "*", "+", "7896543", "*", "157984320", "+" };

        Num p = evaluatePostfix(input);
        System.out.println((p.isNegative?"-":"")+p);
        System.out.println(squareRoot(p));
        (p.convertBase(1000)).printList();

    }

}
