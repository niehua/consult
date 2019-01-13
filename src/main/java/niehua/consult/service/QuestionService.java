package niehua.consult.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import niehua.consult.entity.Answer;
import niehua.consult.entity.Keyword;
import niehua.consult.entity.Question;
import niehua.consult.entity.User;
import niehua.consult.mapper.AnswerMapper;
import niehua.consult.mapper.QuestionMapper;
import niehua.consult.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @description: 问题业务层
 * @author: sheep
 * @date: create in 21:31 2018/7/19
 */

@Service
@Transactional
public class QuestionService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AnswerService answerService;

    @Resource
    private KeywordService keywordService;

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private AnswerMapper answerMapper;

    @Value("${original-keyword-list-size}")
    private int MAX_SIZE;//用户输入原始关键字的最多关键字个数

    @Value("${page-size-question-list}")
    private int PAGE_SIZE;
    
    /*
    功能：根据用户传入word给出相关问题，并按相关度排序
    实现：1.提取word的关键字keywords1
         2.从table_keyword中找出like keywords1对应的keywords2
         3.根据keywords2计算weightAndQuestionIdList
         4.对weightAndQuestionIdList按权值由大到小排序，提取出questionIdList
         5.根据questionIdList，得到questionList
     */
    public PageInfo<Question> getQuestions(String word, int currentPage) {
        List<String> keywords1 = keywordService.extractKeyword(word, MAX_SIZE);//拆分成关键字
        System.out.println("###输入word中的关键字###");        
        System.out.println(keywords1);
        System.out.println("###表中对应关键字###");
        List<Keyword> keywords2 = new LinkedList<>();
        for (String s : keywords1) {
            keywords2.addAll(keywordService.getRelevantKeywordList(s));
        }
        for (Keyword s : keywords2) {
            System.out.print(s.getId() + " " + s.getContent()+" | ");
        }
        //计算权重
        /*for (int i = 0; i < keywords2.size() - 1; i++) {
            Keyword ikeyword = keywords2.get(i);
            for (int j = keywords2.size() - 1; j > i; j--) {
                Keyword jkeyword = keywords2.get(j);
                if (ikeyword.getQuestionId().equals(jkeyword.getQuestionId())) {
                    ikeyword.setWeight(ikeyword.getWeight() + jkeyword.getWeight());
                    keywords2.remove(j);//没有快速失败错误？
                }
            }
        }*/
        int len = keywords2.size() - 1;
        for (int i=0; i<len; ++i) {
            Keyword kw1 = keywords2.get(i);
            if(kw1.getWeight() == null)
            	continue;
            for (int j=i+1; j<len; ++j) {
                Keyword kw2 = keywords2.get(j);
                if (kw1.getQuestionId().equals(kw2.getQuestionId())) {
                	kw1.setWeight(kw1.getWeight() + kw2.getWeight());
                    kw2.setWeight(null);
                }
            }
        }
        Iterator<Keyword> it = keywords2.iterator();
		while(it.hasNext()){
			Keyword kw = it.next();
			if(kw.getWeight() == null)
				it.remove();
        }    
        keywords2.sort(new Comparator<Keyword>() {//比较器：相关度从高到低排序
            @Override
            public int compare(Keyword o1, Keyword o2) {
            	return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        List<Question> questions = new LinkedList<>();
        for (Keyword kw : keywords2) {
        	questions.add(getHighAnswer(kw.getQuestionId()));//对每个问题，设置两个评分最高的回答
        } 
        System.out.println("\n###最终按相关性排列的问题序列号及内容###");
        for (Question q : questions) {
            System.out.println(q.getId() + " " + q.getContent());
        }

        int maxPage =  questions.size()/ PAGE_SIZE + 1;
        int start = (currentPage - 1) * PAGE_SIZE;
        int end   = currentPage * PAGE_SIZE;
        if (currentPage >= maxPage) 
            end = questions.size();
        questions = questions.subList(start, end);
        PageInfo<Question> questionPageInfo = new PageInfo<>(questions);
        questionPageInfo.setPages(maxPage);
        return questionPageInfo;
    }
    
    /*
    选择问题中评分最高的两个回答
     */
    public Question getHighAnswer(Integer questionId) {
        Question question = questionMapper.selectByPrimaryKey(questionId);//查询问题的所有答案
        List<Answer> answerList = answerMapper.selectByQuestion(question);
        answerList.sort(new Comparator<Answer>() {
            @Override
            public int compare(Answer o1, Answer o2) {
                int praise1 = o1.getLikeNumber();
                int praise2 = o2.getLikeNumber();
                if (praise1 <= praise2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        if (answerList.size() > 2) {
            answerList = answerList.subList(0, 2);
        }
        question.setAnswerList(answerList);
        return question;
    }
    /*
    传入用户id，提问内容，提问
     */
    public Question askQuestion(String authorId, String content) {
        Question question = new Question();
        question.setAuthorId(authorId);
        question.setContent(content);
        System.out.println("-----------"+question.getId());
        questionMapper.insert(question);//此时自动拿到插入数据库时自增的主键值
        System.out.println("+++++++++++"+question.getId());
        keywordService.processKeywordOfQuestion(question);
        return question;
    }

    /*
    根据用户id，检索其所有提出的问题，并精简答案返回,分页显示
     */
    public PageInfo<Question> getUserQuestion(String authorId, Integer currentPage) {
        User user = userMapper.selectByPrimaryKey(authorId);
        PageHelper.startPage(currentPage, PAGE_SIZE);
        List<Question> questionList = questionMapper.selectByAuthorId(user);
        for (Question aQuestionList : questionList) {
            Question question = aQuestionList;
            question = getHighAnswer(question.getId());
            aQuestionList.setAnswerList(question.getAnswerList());
        }
        return new PageInfo<>(questionList);
    }
   
    /*
    查看问题详情时使用，返回所有答案
     */
    public Question getIntactQuestion(String visitorId, Integer questionId, Integer currentPage) {
        Question question = questionMapper.selectByPrimaryKey(questionId);
        List<Answer> answerList = answerMapper.selectByQuestion(question);
        answerList = answerService.setLikedList(visitorId, answerList);
        question.setAnswerList(answerList);
        return question;
    }

    /*
    返回最新且未被回答的问题
     */
    public PageInfo<Question> getNewEmptyQuestionList(Integer currentPage) {
        PageHelper.startPage(currentPage, PAGE_SIZE);
        List<Question> questionList = questionMapper.selectEmptyQuestion();
        return new PageInfo<>(questionList);
    }
    
}
