Option Explicit
'***********************************************************
' ����  : �댇�i���o�͌���c�[���@���[�����M�X�N���v�g
' �T�v  : ALC��M�t�@�C���捞�����Ɏ��s�����Ƃ��ɋN������A�֌W�҂Ƀ��[���Œʒm����
' �쐬��: 2017/12/01  �쐬��: DA
'***********************************************************

'*******************************************************************************
'                             �萔��`
'*******************************************************************************
' �e�֐��߂�l��`
Const iRetOK = 0                        '����
Const iRetNG = -1                       '�ُ�

' ���[���A�h���X��`�i���M�敡���̏ꍇ�A�J���}��؂�j
Const mailTo = "kenji.nagai@dragonagency.co.jp, kenji.nagai@dragonagency.co.jp"
Const mailFrom = "kenji.nagai@dragonagency.co.jp, kenji.nagai@dragonagency.co.jp"
Const title = "�y�e�X�g�z�댇�i���o�͌���c�[�� - �G���[�ʒm"
Const message = "�B�e�摜�̎捞�����ɂăG���[���������܂����B"

'***********************************************************
' �֐����Fcmn_sendMessage
' �T�v  �F�����̓��e�����[������
' ����  �F1) ���[�����M��
'       �F2) ���[����M��
'       �F3) ���[���^�C�g��
'       �F4) ���[�����b�Z�[�W
' �߂�l�F�Ȃ�
' �쐬���F2017/12/01  �쐬��  �FDA
' �X�V���F�@�@�@�@�@�@�X�V��  �F
'�@�@�@�@�@�@�@�@�@�@ �X�V���e�F
'***********************************************************
Sub cmn_sendMessage(FromMlAddr, ToMlAddr, Title, Message)
    Dim oMsg
    Set oMsg = CreateObject("CDO.Message")
    oMsg.BodyPart.Charset = "ISO-2022-JP"           '���M�����R�[�h
    oMsg.From = FromMlAddr
    oMsg.To = ToMlAddr
    oMsg.Subject = Title
    oMsg.TextBody = Message
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendusing") = 2                                      '���M���@�B1:���[�J��SMTP�T�[�r�X�A2:�O��SMTP�T�[�o�[�A3:OLE DB�𗘗p���ă��[�J����Exchange�ɐڑ�����
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpconnectiontimeout") = 30                         'SMTP�T�[�o�[�Ƃ̐ڑ��^�C���A�E�g����
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserver") = "smtp.gmoserver.jp"                   'SMTP�T�[�o�[�̃A�h���X
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpserverport") = 587                               'SMTP�T�[�o�[�̃|�[�g
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpauthenticate") = 1                               'SMTP�F�� 1:Basic�F�� / 2:NTLM�F��
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendusername") = "kenji.nagai@dragonagency.co.jp"    'SMTP�F�؃A�J�E���g
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/sendpassword") = "vly$auA8"                          'SMTP�F�؃p�X���[�h
    oMsg.Configuration.Fields.Item("http://schemas.microsoft.com/cdo/configuration/smtpusessl") = False                                 'SSL�̗��p True :�g�p / False:���g�p
    oMsg.Configuration.Fields.Update
    oMsg.Send
End Sub

'*******************************************************************************
'                             �����J�n
'*******************************************************************************
'�p�����[�^�������ꍇ�ł����삳����悤�ɂ���B
On Error Resume Next
Dim body
Dim bodyItem
Dim item
For Each item In Wscript.Arguments
    bodyItem = bodyItem & item & vbCrLf
Next
On Error GoTo 0
body = message & vbCrLf & vbCrLf & bodyItem

'���[�����M
call cmn_sendMessage(mailFrom, mailTo, title, body)

