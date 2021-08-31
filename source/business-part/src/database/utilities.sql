-- синхронизация культуры (шаблоны + работы в шаблонах)

with
  fid as (select id from item where name='Огуречник' and owner_id=3),
  tid as (select id from item where name='Огуречник' and owner_id is null),
  ite_update as (
		update item
            set description=(select description from item where id=(select id from fid))
        where id=(select id from tid)
  ),
  old_system_pattern as (select id from user_work where species_id in (select id from tid) and pattern=true),
  user_pattern as (select id from user_work where species_id in (select id from fid) and pattern=true),
  del_old_system_patt as (delete from user_work where id in (select id from old_system_pattern) and user_id is null),
  copy_user_for_to_system as (
        update user_work set species_id=(select id from tid), user_id=null
        where species_id=(select id from fid) and user_id=3 and pattern=true
  )
  update user_task set owner_id=null where user_work_id in (select id from user_pattern);