package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guan
 * @date 4/13/17
 */

public class ReadFromFile {

    private static Logger logger = LoggerFactory.getLogger(ReadFromFile.class);

    /**
     * 对于windows下，\r\n这两个字符在一起时，表示一个换行。
     * 但如果这两个字符分开显示时，会换两次行。
     * 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
     */

    /**
     * 随机读取文件内容
     * <p>
     * 未完待续……
     *
     * @param filePath
     */
    public static void readFileByRandomAccess(String filePath) {

        checkIfRunningInMainThread();


        RandomAccessFile randomFile = null;
        try {
            printLog("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(filePath, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 0 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     *
     * @param filePath
     * @return
     */
    public static List<String> readFileByLines(String filePath) {

        checkIfRunningInMainThread();


        File file = new File(filePath);

        if (!file.exists()) {
            printLog("文件不存在，读取失败！");
            return null;
        }

        BufferedReader reader = null;

        List<String> lines = new ArrayList<>();

        int line = 0;

        try {
            printLog("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                //printLog("line " + line + ": " + tempString);
                lines.add(tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            printLog("数据读取完毕，行数：" + line);
            return lines;
        }
    }


    /**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     *
     * @param filePath
     */
    public static void readFileByBytes(String filePath, FileListener listener) {

        checkIfRunningInMainThread();

        File file = new File(filePath);
        if (!file.exists()) {
            printLog("文件不存在，读取失败！");
            return;
        }

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            printLog("以字节为单位读取文件内容，一次读多个字节：");
            // 一次读多个字节
            byte[] bytes = new byte[1024];
            int length = 0;
            fileInputStream = new FileInputStream(filePath);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            ReadFromFile.printAvailableBytes(bufferedInputStream);
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((length = bufferedInputStream.read(bytes)) != -1) {

                listener.onReadByBytesCallback(bytes, false);
                //System.out.write(bytes, 0, len);//好方法，第一个参数是数组，第二个参数是开始位置，第三个参数是长度
            }
            listener.onReadByBytesCallback(bytes, true);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
            }

        }
    }


    /**
     * 以字符的形式读取文件
     *
     * @param filePath
     */
    public static void readFileByChars(String filePath, FileListener listener) {

        checkIfRunningInMainThread();


        FileReader fileReader = null;
        char[] cbuf = new char[1024];
        //len表示每次读如的数据长度，如果等于-1，则表示文件读取完毕
        int length = 0;
        try {
            fileReader = new FileReader(filePath);
            //每次把读入的数据存放到cbuf中，
            while ((length = fileReader.read(cbuf)) != -1) {
                //如何对读取的数据进行处理？
                listener.onReadByCharsCallback(cbuf, false);
            }
            listener.onReadByCharsCallback(cbuf, true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件最后N行
     * <p>
     * 根据换行符判断当前的行数，
     * 使用统计来判断当前读取第N行
     * <p>
     * PS:输出的List是倒叙，需要对List反转输出
     *
     * @param file    待文件
     * @param numRead 读取的行数
     * @return List<String>
     */
    public static List<String> readLastNLine(File file, long numRead) {

        // 定义结果集
        List<String> result = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        //行数统计
        long count = 0;

        // 排除不可读状态
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            return result;
        }

        RandomAccessFile fileRead = null;
        try {
            //使用读模式
            fileRead = new RandomAccessFile(file, "r");
            //读取文件长度
            long length = fileRead.length();
            //如果是0，代表是空文件，直接返回空结果
            if (length == 0L) {
                return result;
            } else {
                //初始化游标
                long pos = length - 1;
                while (pos > 0) {
                    pos--;
                    //开始读取
                    fileRead.seek(pos);
                    //如果读取到\n代表是读取到一行
                    if (fileRead.readByte() == '\n') {
                        //使用readLine获取当前行
                        String line = new String(fileRead.readLine().getBytes("ISO-8859-1"), "utf-8");
                        //将结果翻转
                        sb.setLength(0);
                        sb.append(line);
                        //保存结果
                        result.add(sb.reverse().toString());
                        //打印当前行
                        System.out.println(line);
                        //行数统计，如果到达了numRead指定的行数，就跳出循环
                        count++;
                        if (count == numRead) {
                            break;
                        }
                    }
                }
                if (pos == 0) {
                    fileRead.seek(0);
                    result.add(fileRead.readLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileRead != null) {
                try {
                    //关闭资源
                    fileRead.close();
                } catch (Exception e) {
                }
            }
        }

        return result;
    }

    /**
     * 显示输入流中还剩的字节数
     *
     * @param in
     */
    private static void printAvailableBytes(InputStream in) {
        try {
            printLog("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printLog(String message) {
        logger.info(message);
    }


    private static void checkIfRunningInMainThread() {

    }

    //=================================================================

    public static class FileListener {

        public void onReadByCharsCallback(char[] chars, boolean isDone) {

        }

        public void onReadByBytesCallback(byte[] bytes, boolean isDone) {

        }
    }

    /**
     * 测试
     *
     * @param args
     */
    @Deprecated
    public static void main(String[] args) {
        File file = new File("C:\\Users\\Guan\\Desktop\\生产数据.txt");
        readLastNLine(file, 20);
    }
}
