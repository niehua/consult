package niehua.consult.service;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import niehua.consult.entity.Answer;
import niehua.consult.entity.Keyword;
import niehua.consult.entity.Question;
import niehua.consult.mapper.KeywordMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description: 关键字业务层
 * @author: sheep
 * @date: create in 17:03 2018/7/26
 */

@Service
@Transactional
public class KeywordService {

    @Resource
    private KeywordMapper keywordMapper;

    @Value("${keyword-list-size}")
    private int SIZE;
    
    /*
    提取关键字，设置最大关键词长度为9
     */
    public List<String> extractKeyword(String content, int maxSize) {
        List<String> keywordContentList = HanLP.extractKeyword(content, maxSize);
        /*
        若提取的关键字个数小于2，则将其分词结果当做关节字
         */
        System.out.println("---------------"+keywordContentList);
        if (keywordContentList.size() <= 2) {
            List<Term> termList = HanLP.segment(content);
            for (Term aTermList : termList) {
                if (!keywordContentList.contains(aTermList.word))
                    keywordContentList.add(aTermList.word);
            }
        }
        System.out.println("+++++++++++++++"+keywordContentList);
        return keywordContentList;
    }

    /*
    在关键字索引表中对每个关键字进行模糊查询，不做重复处理
     */
    public List<Keyword> getRelevantKeywordList(String keywordContent) {
        return keywordMapper.selectByKeyword("%" + keywordContent + "%");
    }
    
    /*
    提取传入问题的关键字并插入库中
     */
    public void processKeywordOfQuestion(Question question) {
        List<String> keywordList = extractKeyword(question.getContent(), SIZE);
        for (int i = 0; i < keywordList.size(); i++) {
            Keyword keyword = new Keyword();
            keyword.setContent(keywordList.get(i));
            keyword.setAnswerId(1);//在问题中没用，在答案中有用
            keyword.setQuestionId(question.getId());
            //keyword.setWeight(2 - (float) i * (float) i / 100);
            keyword.setWeight(2f);
            keyword.setId(null);         
            keywordMapper.insert(keyword);
        }
    }
    
    /*
    提取传入回答的关键字并插入库中
     */
    public void processKeywordOfAnswer(Answer answer) {
        List<String> keywordList = extractKeyword(answer.getContent(), SIZE);
        for (int i = 0; i < keywordList.size(); i++) {
            Keyword keyword = new Keyword();
            keyword.setContent(keywordList.get(i));
            keyword.setAnswerId(answer.getId());
            keyword.setQuestionId(answer.getQuestionId());
            //keyword.setWeight(1 - (float) i * (float) i / 100);
            keyword.setWeight(1f);
            keyword.setId(null);
            keywordMapper.insert(keyword);
        }
    }

    
    /*
    计算某问题对某关键字的相关度
     */
    public Float getWeightOfQuestionForKeyword(Integer questionId, String keywordContent) {
        List<Float> weightListOfQuestionForKeyword = keywordMapper.selectWeightOfQuestionForKeyword(questionId, keywordContent);
        Float weightOfQuestionForKeyword = (float) 0;
        for (Float aWeightListOfQuestionForKeyword : weightListOfQuestionForKeyword)
            weightOfQuestionForKeyword += aWeightListOfQuestionForKeyword;
        return weightOfQuestionForKeyword;
    }
    

    /*
    后台调用，处理库中所有问题及回答测关键字，注意寻找最新插入库中的问题与回答id
    
    public void processKeywordOfAll(Integer startOfQuestionId, Integer startOfAnswerId) {
        List<Integer> questionIdOfAll = questionMapper.selectIdOfAll(startOfQuestionId);
        List<Integer> answerIdOfAll = answerMapper.selectIdOfAll(startOfAnswerId);
        Integer questionId;
        Integer answerId;
        for (Integer aQuestionIdOfAll : questionIdOfAll) {
            questionId = aQuestionIdOfAll;
            processKeywordOfQuestion(questionId);
        }
        for (Integer anAnswerIdOfAll : answerIdOfAll) {
            answerId = anAnswerIdOfAll;
            processKeywordOfAnswer(answerId);
        }
    } */
}
